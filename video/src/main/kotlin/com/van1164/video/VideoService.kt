package com.van1164.video

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
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StopWatch
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.core.scheduler.Schedulers
import com.van1164.common.s3.S3UploadComponent
import org.springframework.http.codec.multipart.FilePart
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class VideoService(
    private val s3Repository: S3UploadComponent,
    private val videoRepository: VideoR2DBCRepository,
    @Value("\${aws.s3.bucketUrl}")
    private val bucketUrl: String,
    private val ffmpeg: FFmpeg,
    private val ffprobe: FFprobe
) {

    val sink = Sinks.many().multicast().onBackpressureBuffer<EventDTO>()
    @Transactional
    fun uploadVideoPartLast(fileUUID : String, totalChunk : Int): Flux<ServerSentEvent<String>> {
        val stopWatch = StopWatch()
        val inputFilePath = Paths.get(UUID.randomUUID().toString() + ".mp4")
        val m3u8Path = "$fileUUID.m3u8"
        val thumbNailPath = UUID.randomUUID().toString() + ".jpg"

        stopWatch.start("mp4로 만드는데 걸린 시간")
        runBlocking {
            Files.createFile(inputFilePath)
        }

        val videoFlux = videoMergeFlux(totalChunk, fileUUID, inputFilePath)
        stopWatch.stop()

        val deleteChunkFileFlux = deleteChunkFileFlux(totalChunk, fileUUID)

        val thumbNailFlux =
            Mono.zip(Mono.just(inputFilePath),Mono.just(thumbNailPath), Mono.just(fileUUID))
                .flatMap{
                    Mono.just(it).doOnNext {
                        createThumbNail(it.t1,it.t2)
                    }
                    .flatMap {
                        saveThumbnailData(it.t3,it.t2)
                    }
                    .subscribeOn(Schedulers.parallel())
            }

        val mp4ToHlsFlux =
            Mono.zip(Mono.just(inputFilePath),Mono.just(m3u8Path),Mono.just(fileUUID))
                .flatMap{ Mono.just(mp4ToHls(it.t1,it.t2,it.t3))
                .subscribeOn(Schedulers.parallel())}
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
        return Flux.range(0, totalChunk).flatMap {
            Flux.just(
                s3Repository.deletePart(fileUUID, it)
            )
        }.doOnComplete {
            sink.tryEmitNext(EventDTO("ing", "썸네일 생성중.."))
        }.subscribeOn(Schedulers.boundedElastic())
    }

    private fun videoMergeFlux(
        totalChunk: Int,
        fileUUID: String,
        inputFilePath: Path
    ) = Flux.range(0, totalChunk)
        .publishOn(Schedulers.boundedElastic())
        .flatMapSequential {
            getPartByteArray(fileUUID, it)
        }
        .doFirst {
            sink.tryEmitNext(EventDTO("ing", "파일 업로드 완료하는 중.."))
        }
        .doOnNext { videoPart ->
            logger.info { "파일write" }
            Mono.fromCallable {
                Files.write(inputFilePath, videoPart, StandardOpenOption.APPEND)
            }.subscribeOn(Schedulers.boundedElastic()).subscribe()
        }

    private fun getPartByteArray(fileUUID: String, it: Int): Flux<ByteArray> {
        val flux = s3Repository.getPartByteArray(
            bucketUrl,
            fileUUID,
            it
        )
        return if (flux != null) {
            Flux.just(flux)
        } else {
            Flux.empty()
        }
    }

    private fun mp4ToHls(
        inputFilePath: Path,
        m3u8Path: String,
        outputUUID: String
    ) {
        logger.info("hls시작")
       val stopWatch = StopWatch()
        stopWatch.start("mp4를 hls로 바꾸고 업로드하는 데 걸린 시간")
        //mp4 to ts


        mp4ToM3U8(inputFilePath, m3u8Path, outputUUID)


        // 여러 TS들을 S3에 업로드
        uploadVideoTs(outputUUID)

        s3Repository.uploadM3U8(m3u8Path, outputUUID)
        stopWatch.stop()
        println(stopWatch.prettyPrint())
    }

    fun createThumbNail(
        inputFilePath: Path,
        thumbNailPath: String
    ) {
        logger.info("썸네일")
        val stopWatch = StopWatch()
        stopWatch.start("썸네일 만들고 업로드하는 데 걸린 시간")
        //thumbnail by ffmpeg

        extractThumbnail(inputFilePath.toString(), thumbNailPath)
        //uploadThumbnail
        s3Repository.uploadThumbnail(thumbNailPath)
        stopWatch.stop()
        println(stopWatch.prettyPrint())
    }

    @Transactional
    fun saveThumbnailData(fileUUID : String,thumbNailUrl : String): Mono<VideoR2dbc> {
        return videoRepository.findFirstByUrl(fileUUID)
            .doOnNext {
                it.thumbNailUrl = s3URL +"thumb/"+ thumbNailUrl
            }
            .flatMap{
                videoRepository.save(it)
            }
    }

    private fun uploadVideoTs(outputUUID: String) {
        val futures = mutableListOf<CompletableFuture<Void>>()
        var count = 0
        while (true) {
            val tsPath = outputUUID + "_" + count.toString().padStart(3, '0') + ".ts"
            val tsFile = File(tsPath)
            if (tsFile.isFile) {
                futures.add(
                    CompletableFuture.runAsync {
                        s3Repository.uploadVideoTs("$outputUUID/$tsPath", tsFile)
                        tsFile.delete()
                    }
                )
                count++
            } else {
                break
            }

        }

        CompletableFuture.allOf(*futures.toTypedArray()).get()
    }

    fun uploadVideoPart(video: FilePart, videoData: UploadVideoPartDTO): Mono<ResponseEntity<HttpStatus>> {
        logger.info { videoData.chunkNumber }
        return s3Repository.uploadVideoPart(video, videoData.chunkNumber,videoData.fileUUID)
        .map{
            if(it){
                ResponseEntity<HttpStatus>(HttpStatus.OK)
            }
            else {
                ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST)
            }
        }.subscribeOn(Schedulers.boundedElastic())

    }
    @Transactional
    fun saveVideoData(
        title: String,
        fileUUID: String,
        userName: String
    ): Mono<ResponseEntity<HttpStatus>> {
        return videoRepository.save(
            VideoR2dbc(url = fileUUID, title = title, userName = userName,thumbNailUrl = null)
        )
            .map{
                ResponseEntity.ok().build<HttpStatus>()
            }
            .onErrorReturn(ResponseEntity.badRequest().build())
    }

    private fun extractThumbnail(inputFilePath: String, thumbNailPath: String) {
        FFmpegBuilder()
            .setInput(inputFilePath)
            .addOutput(thumbNailPath)
            .addExtraArgs("-ss", "00:00:1")
            .addExtraArgs("-vframes", "1")
            .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
            .done()
            .apply {
                FFmpegExecutor(ffmpeg, ffprobe).createJob(this).run()
            }

    }



    private fun mp4ToM3U8(inputFilePath: Path, m3u8Path: String, tsFilePath: String) {
        val builder = FFmpegBuilder()
            .setInput(inputFilePath.toString())
            .addOutput(m3u8Path)
            .addExtraArgs("-c", "copy")
            .addExtraArgs("-bsf:v", "h264_mp4toannexb")
            .addExtraArgs("-hls_segment_filename", "${tsFilePath}_%03d.ts")
            .addExtraArgs("-start_number", "0")
            .addExtraArgs("-hls_time", "5")
            .addExtraArgs("-hls_list_size", "0")
            .addExtraArgs("-hls_base_url", "https://video-stream-spring.s3.ap-northeast-2.amazonaws.com/$tsFilePath/")
            .addExtraArgs("-f", "hls")
            .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL).done()
        FFmpegExecutor(ffmpeg, ffprobe).createJob(builder).run()
        File(inputFilePath.toString()).delete()
    }
    fun findAll(sort : Sort): Flux<VideoR2dbc> {
        return videoRepository.findAll(sort)
    }

    fun findById(detailId: String): Mono<VideoR2dbc> {
        return videoRepository.findFirstByUrl(detailId)
    }


}