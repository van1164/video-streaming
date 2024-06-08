package com.van1164.video.upload

import com.van1164.common.domain.VideoR2dbc
import com.van1164.common.dto.EventDTO
import com.van1164.common.dto.UploadVideoPartDTO
import com.van1164.common.util.Utils.logger
import com.van1164.util.s3URL
import kotlinx.coroutines.*
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.core.scheduler.Schedulers
import com.van1164.common.s3.S3UploadComponent
import com.van1164.common.util.Utils
import com.van1164.video.video.VideoService
import org.springframework.http.codec.multipart.FilePart
import reactor.kotlin.core.publisher.toMono
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import reactor.kotlin.core.util.function.*
import reactor.util.function.Tuple2

@Service
class UploadService(
    private val s3UploadComponent: S3UploadComponent,
    private val videoService: VideoService,
    @Value("\${aws.s3.bucketUrl}")
    private val bucketUrl: String,
    private val ffmpeg: FFmpeg,
    private val ffprobe: FFprobe
) {

    val sink: Sinks.Many<EventDTO> = Sinks.many().multicast().onBackpressureBuffer()
    @Transactional
    fun uploadVideoPartLast(fileUUID : String, totalChunk : Int): Flux<ServerSentEvent<String>> {
        val inputFilePath = Paths.get(Utils.createVideoPath())
        val m3u8Path = Utils.createFilePath(fileUUID,"m3u8")
        val thumbNailPath = Utils.createImagePath()
        runBlocking {
            Files.createFile(inputFilePath)
        }

        val videoFlux = videoMergeFlux(totalChunk, fileUUID, inputFilePath)

        val deleteChunkFileFlux = deleteChunkFileFlux(totalChunk, fileUUID)

        val thumbNailFlux =
            createThumbNail(inputFilePath,thumbNailPath)
                .flatMap {
                    saveThumbnailData(fileUUID,thumbNailPath)
                }
                .subscribeOn(Schedulers.parallel())


        val mp4ToHlsFlux =
            mp4ToHls(inputFilePath,m3u8Path,fileUUID)
                .subscribeOn(Schedulers.parallel())
                .doFirst { sink.tryEmitNext(EventDTO("ing","파일 변환 처리중...")) }


        Flux.concat(videoFlux,Flux.merge(deleteChunkFileFlux,thumbNailFlux,mp4ToHlsFlux))
            .doOnComplete {
                sink.tryEmitNext(EventDTO("finish",fileUUID))
            }
            .subscribe()
        return sink.asFlux().map{event->
            ServerSentEvent.builder<String>(event.message)
                .event(event.event)
                .build()
            }


    }

    private fun deleteChunkFileFlux(totalChunk: Int, fileUUID: String): Flux<Unit> {
        return Flux.range(0, totalChunk)
            .flatMap {
                Flux.just(
                    s3UploadComponent.deletePart(fileUUID, it)
                )
            }.doOnComplete {
                sink.tryEmitNext(EventDTO("ing", "썸네일 생성중.."))
            }.subscribeOn(Schedulers.boundedElastic())
    }

    private fun videoMergeFlux(
        totalChunk: Int,
        fileUUID: String,
        inputFilePath: Path
    ): Flux<Path> {
        return Flux.range(0, totalChunk)
            .publishOn(Schedulers.boundedElastic())
            .flatMapSequential {
                getPartByteArray(fileUUID, it)
            }
            .doFirst {
                sink.tryEmitNext(EventDTO("ing", "파일 업로드 완료하는 중.."))
            }
            .flatMap { videoPart ->
                Mono.fromCallable {
                    Files.write(inputFilePath, videoPart, StandardOpenOption.APPEND)
                }.subscribeOn(Schedulers.boundedElastic())
            }
    }

    private fun getPartByteArray(fileUUID: String, it: Int): Flux<ByteArray> {
        return s3UploadComponent.getPartByteArray(
            bucketUrl,
            fileUUID,
            it
        )?.let{Flux.just(it)} ?:let{Flux.empty()}
    }

    private fun mp4ToHls(
        inputFilePath: Path,
        m3u8Path: String,
        outputUUID: String
    ): Mono<File> {
        //mp4 to ts
        return mp4ToM3U8(inputFilePath, m3u8Path, outputUUID)
            .flatMap {
                uploadVideoTs(outputUUID)
            }
            .flatMap {
                s3UploadComponent.uploadM3U8(m3u8Path, outputUUID)
            }
    }

    fun createThumbNail(
        inputFilePath: Path,
        thumbNailPath: String
    ): Mono<File> {
        //thumbnail by ffmpeg
        return extractThumbnail(inputFilePath.toString(), thumbNailPath)
            .flatMap{
                //uploadThumbnail
                s3UploadComponent.uploadThumbnail(thumbNailPath)
            }

    }

    @Transactional
    fun saveThumbnailData(fileUUID : String,thumbNailUrl : String): Mono<VideoR2dbc> {
        return videoService.findFirstByUrl(fileUUID)
            .doOnNext {
                it.thumbNailUrl = s3URL +"thumb/"+ thumbNailUrl
            }
            .flatMap{
                videoService.save(it)
            }
    }

    private fun uploadVideoTs(outputUUID: String): Mono<Tuple2<String, File>> {
        return Flux.range(0, Int.MAX_VALUE)
            .flatMap{count->
                val tsPath = Utils.createFilePath(outputUUID + "_" + count.toString().padStart(3, '0'),"ts")
                Mono.zip(tsPath.toMono(),File(tsPath).toMono())
            }
            .takeWhile{ it.t2.isFile}
            .filter { it.t2.isFile }
            .doOnNext {(tsPath,tsFile)->
                s3UploadComponent.uploadVideoTs("$outputUUID/$tsPath", tsFile)
                tsFile.delete()
            }
            .last()

    }

    fun uploadVideoPart(video: FilePart, videoData: UploadVideoPartDTO): Mono<ResponseEntity<Boolean>> {
        return s3UploadComponent.uploadVideoPart(video, videoData.chunkNumber,videoData.fileUUID)
        .map{
            if(it){
                ResponseEntity.ok(it)
            }
            else {
                ResponseEntity.badRequest().body(false)
            }
        }.subscribeOn(Schedulers.boundedElastic())

    }
    @Transactional
    fun saveVideoData(
        title: String,
        fileUUID: String,
        userName: String
    ): Mono<ResponseEntity<Boolean>> {
        return videoService.save(
            VideoR2dbc(url = fileUUID, title = title, userName = userName,thumbNailUrl = null)
        )
            .map{
                ResponseEntity.ok(true)
            }
            .onErrorReturn(ResponseEntity.badRequest().body(false))
    }

    private fun extractThumbnail(inputFilePath: String, thumbNailPath: String): Mono<FFmpegBuilder> {
        return FFmpegBuilder()
            .setInput(inputFilePath)
            .addOutput(thumbNailPath)
            .addExtraArgs("-ss", "00:00:1")
            .addExtraArgs("-vframes", "1")
            .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
            .done()
            .toMono()
            .doOnNext {builder->
                FFmpegExecutor(ffmpeg, ffprobe).createJob(builder).run()
            }

    }



    private fun mp4ToM3U8(inputFilePath: Path, m3u8Path: String, tsFilePath: String): Mono<Unit> {
        return FFmpegBuilder()
            .setInput(inputFilePath.toString())
            .addOutput(m3u8Path)
            .addExtraArgs("-c", "copy")
            .addExtraArgs("-bsf:v", "h264_mp4toannexb")
            .addExtraArgs("-hls_segment_filename", "${tsFilePath}_%03d.ts")
            .addExtraArgs("-start_number", "0")
            .addExtraArgs("-hls_time", "5")
            .addExtraArgs("-hls_list_size", "0")
            .addExtraArgs("-hls_base_url", "$bucketUrl$tsFilePath/")
            .addExtraArgs("-f", "hls")
            .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL).done()
            .toMono()
            .doOnNext { logger.info{"mp4ToM3U*"} }
            .map {builder->
                FFmpegExecutor(ffmpeg, ffprobe).createJob(builder).run()
            }
            .doOnNext{
                File(inputFilePath.toString()).delete()
            }

    }

    fun findById(detailId: String): Mono<VideoR2dbc> {
        return videoService.findFirstByUrl(detailId)
    }


}