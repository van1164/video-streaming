package com.KY.KoreanYoutube.upload

import com.KY.KoreanYoutube.domain.Video
import com.KY.KoreanYoutube.upload.dto.UploadVideoPartDTO
import com.KY.KoreanYoutube.video.VideoRepository
import kotlinx.coroutines.*
import mu.KotlinLogging
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Service
import org.springframework.util.StopWatch
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.core.scheduler.Schedulers
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.jvm.optionals.getOrNull

private val logger = KotlinLogging.logger {} // KotlinLogging 사용

data class Event(
    val event : String,
    val message : String
)

@Service
class UploadService(
    val uploadRepository: UploadRepository,
    val videoRepository: VideoRepository,
    @Value("\${aws.s3.bucketUrl}")
    val bucketUrl: String,
    val ffmpeg: FFmpeg,
    val ffprobe: FFprobe
) {

    val sink = Sinks.many().multicast().onBackpressureBuffer<Event>()
    fun uploadVideoPartLast(fileUUID : String, totalChunk : Int): Flux<ServerSentEvent<String>> {
        //여러 part를 하나의 파일로 만들기
        val stopWatch = StopWatch()
        stopWatch.start("mp4로 만드는데 걸린 시간")
        //val mp4start = System.currentTimeMillis()
        val inputFilePath = Paths.get(UUID.randomUUID().toString() + ".mp4")
        runBlocking {
            Files.createFile(inputFilePath)
        }

        val videoFlux = Flux.range(0,totalChunk)
            .publishOn(Schedulers.boundedElastic())
            .flatMapSequential {
                val flux = uploadRepository.getPartByteArray(
                    bucketUrl,
                    fileUUID,
                    it
                )
                if(flux !=null){
                    Flux.just(flux)
                }
                else{
                    Flux.empty()
                }
            }
            .doFirst {
                sink.tryEmitNext(Event("ing" ,"파일 업로드 완료하는 중.."))
            }
            .doOnNext{videoPart->
                logger.info{"파일write"}
                Mono.fromCallable {
                    Files.write(inputFilePath, videoPart, StandardOpenOption.APPEND)
                }.subscribeOn(Schedulers.boundedElastic()).subscribe()
            }


        stopWatch.stop()
        val m3u8Path = "$fileUUID.m3u8"
        val thumbNailPath = UUID.randomUUID().toString() + ".jpg"

        val deleteChunkFileFlux =
            Flux.range(0,totalChunk).flatMap{
                Flux.just(
                    uploadRepository.deletePart(fileUUID, it)
                )
            }.doOnComplete {
                sink.tryEmitNext(Event("ing","썸네일 생성중.."))
            }
                .subscribeOn(Schedulers.boundedElastic())

        val thumbNailFlux = Mono.zip(Mono.just(inputFilePath),Mono.just(thumbNailPath))
            .flatMap{
                Mono.just(createThumbNail(it.t1,it.t2)).subscribeOn(Schedulers.parallel())
            }
        val saveVideoFlux = Mono.zip(Mono.just(fileUUID),Mono.just(thumbNailPath))
            .flatMap { Mono.just(saveThumbnailData(it.t1,it.t2)).subscribeOn(Schedulers.parallel()) }

        val mp4ToHlsFlux = Mono.zip(Mono.just(inputFilePath),Mono.just(m3u8Path),Mono.just(fileUUID))
            .flatMap{ Mono.just(mp4ToHls(it.t1,it.t2,it.t3)).subscribeOn(Schedulers.parallel())}.doFirst { sink.tryEmitNext(Event("ing","파일 변환 처리중...")) }

        Flux.concat(videoFlux,Flux.merge(deleteChunkFileFlux,thumbNailFlux,saveVideoFlux,mp4ToHlsFlux))
            .doOnComplete {
                sink.tryEmitNext(Event("finish",fileUUID))
            }
            .subscribe()
        return sink.asFlux().map{event->
            ServerSentEvent.builder<String>(event.message)
                .event(event.event)
                .build()
        }

//        val futureList = mutableListOf<CompletableFuture<ByteArray>>()


//        for (i: Int in 0 until videoData.totalChunk) {
//            futureList.add(CompletableFuture.supplyAsync {
//                return@supplyAsync uploadRepository.getPartByteArray(
//                    bucketUrl,
//                    videoData.fileUUID,
//                    i
//                )
//            })
//        }
//
//
//        return CompletableFuture.allOf(*futureList.toTypedArray())
//            .thenApply {
//                // ts -> mp4
//                futureList.forEach{videoPart ->
//                    Files.write(inputFilePath, videoPart.get(), StandardOpenOption.APPEND)
//                }
//                stopWatch.stop()
//            }.thenApplyAsync {
//                val outputUUID = UUID.randomUUID().toString()
//                val m3u8Path = "$outputUUID.m3u8"
//                val thumbNailPath = UUID.randomUUID().toString() + ".jpg"
//                val deleteChunkFuture = CompletableFuture.runAsync{deleteChunkFiles(videoData)}
//                val thumbNailFuture = CompletableFuture.runAsync{createThumbNail(inputFilePath, thumbNailPath)}
//                val saveDataFuture = CompletableFuture.runAsync{saveVideoData(outputUUID, videoData, thumbNailPath)}
//                val mp4ToHlsFuture = CompletableFuture.runAsync{mp4ToHls(inputFilePath, m3u8Path, outputUUID)}
//                CompletableFuture.allOf(deleteChunkFuture,thumbNailFuture,saveDataFuture,mp4ToHlsFuture).get()
//                println(stopWatch.prettyPrint())
//                return@thenApplyAsync outputUUID
//            }.get()

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

        uploadRepository.uploadM3U8(m3u8Path)
        stopWatch.stop()
        println(stopWatch.prettyPrint())
    }

    private fun createThumbNail(
        inputFilePath: Path,
        thumbNailPath: String
    ) {
        logger.info("썸네일")
        val stopWatch = StopWatch()
        stopWatch.start("썸네일 만들고 업로드하는 데 걸린 시간")
        //thumbnail by ffmpeg

        extractThumbnail(inputFilePath.toString(), thumbNailPath)
        //uploadThumbnail
        uploadRepository.uploadThumbnail(thumbNailPath)
        stopWatch.stop()
        println(stopWatch.prettyPrint())
    }

    private fun saveThumbnailData(fileUUID : String,thumbNailUrl : String){
        val video = videoRepository.findById(fileUUID).getOrNull()
        if(video != null){
            video.thumbNailUrl =thumbNailUrl
        }
    }

    private fun deleteChunkFiles(
        videoData: UploadVideoPartDTO
    ) {
        logger.info("DELETE")
        val futures = (0 until videoData.totalChunk).map {
            CompletableFuture.runAsync { uploadRepository.deletePart(videoData.fileUUID, it) }
        }
        CompletableFuture.allOf(*futures.toTypedArray()).get()
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
                        uploadRepository.uploadVideoTsVer2(tsPath, tsFile)
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

    fun uploadVideoPart(video: MultipartFile, videoData: UploadVideoPartDTO): Mono<ResponseEntity<HttpStatus>> {
//        val testPath = Paths.get("test_" +videoData.chunkNumber.toString())
//        Files.copy(video.inputStream,testPath)
        return Mono.just(
            uploadRepository.uploadVideoPart(video, videoData.chunkNumber,videoData.fileUUID)
        ).map{
            if(it){
                ResponseEntity<HttpStatus>(HttpStatus.OK)
            }
            else {
                ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST)
            }
        }.subscribeOn(Schedulers.boundedElastic())

    }

    fun uploadVideo(video: MultipartFile, videoData: UploadVideoPartDTO): ResponseEntity<Any> {
        uploadRepository.uploadVideoPart(video, videoData.chunkNumber,videoData.fileUUID)

        if (videoData.totalChunk - 1 == videoData.chunkNumber) {
            //여러 part를 하나의 파일로 만들기
            val stopWatch = StopWatch()
            stopWatch.start("mp4로 만드는데 걸린 시간")
            //val mp4start = System.currentTimeMillis()
            val inputFilePath = Paths.get(UUID.randomUUID().toString() + ".mp4")
            Files.createFile(inputFilePath)
            for (i: Int in 0 until videoData.totalChunk) {
                val videoPart = uploadRepository.getPart(bucketUrl, videoData.fileUUID, i) ?: return ResponseEntity(
                    HttpStatus.BAD_REQUEST
                )
                Files.write(inputFilePath, videoPart.readAllBytes(), StandardOpenOption.APPEND)
                uploadRepository.deletePart(videoData.fileUUID, i)
            }
            stopWatch.stop()
            //logger.info{"mp4로 만드는데 걸린 시간: " + (System.currentTimeMillis() - mp4start)}

            //m3u8과 ts들로 변경해야함
            val outputUUID = UUID.randomUUID().toString()
            val m3u8Path = "$outputUUID.m3u8"
            println(inputFilePath.toString())

            stopWatch.start("썸네일 만들고 업로드하는 데 걸린 시간")
            //val thumbNailstart = System.currentTimeMillis()
            //thumbnail by ffmpeg
            val thumbNailPath = UUID.randomUUID().toString() + ".jpg"
            extractThumbnail(inputFilePath.toString(), thumbNailPath)
            //uploadThumbnail
            uploadRepository.uploadThumbnail(thumbNailPath)
            stopWatch.stop()
            //logger.info{"썸네일 만들고 업로드하는 데 걸린 시간: " + (System.currentTimeMillis() - thumbNailstart)}

            stopWatch.start("mp4를 hls로 바꾸고 업로드하는 데 걸린 시간")
            //val hlsUploadStart = System.currentTimeMillis()
            //mp4 to ts
            mp4ToM3U8(inputFilePath, m3u8Path, outputUUID)


            // 여러 TS들을 S3에 업로드
            uploadRepository.uploadVideoTs(outputUUID)
            uploadRepository.uploadM3U8(m3u8Path)
            println("https://video-stream-spring.s3.ap-northeast-2.amazonaws.com/$m3u8Path")
            stopWatch.stop()
            //saveVideoData(videoData, thumbNailPath)
            println(stopWatch.prettyPrint())
            return ResponseEntity(outputUUID, HttpStatus.OK)
        } else {
            return ResponseEntity(HttpStatus.PARTIAL_CONTENT)
        }
    }

    fun saveVideoData(
        fileUUID: String,
        title : String
    ) {
        videoRepository.save(
            Video(
                createDate = Date.from(
                    LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
                ), id = fileUUID, title = title, thumbNailUrl = null
            )
        )
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

//    private fun divideTsFile(tsFilePath: String) {
//        val segmentBuilder =
//            FFmpegBuilder().setInput(tsFilePath)
//                .addOutput("${tsFilePath}_%03d.ts")
//                .addExtraArgs("-c", "copy")
//                .addExtraArgs("-map", "0")
//                .addExtraArgs("-segment_time", "5")
//                .addExtraArgs("-f", "segment")
//                .addExtraArgs("-reset_timestamps", "1")
//                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
//                .done()
//        FFmpegExecutor(ffmpeg, ffprobe).createJob(segmentBuilder).run()
//        File(tsFilePath).delete()
//    }

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
            .addExtraArgs("-hls_base_url", "https://video-stream-spring.s3.ap-northeast-2.amazonaws.com/")
            .addExtraArgs("-f", "hls")
            .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL).done()
        FFmpegExecutor(ffmpeg, ffprobe).createJob(builder).run()
        File(inputFilePath.toString()).delete()
    }


}