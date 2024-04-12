package com.KY.KoreanYoutube.stream

import com.KY.KoreanYoutube.domain.LiveStream
import com.KY.KoreanYoutube.dto.StreamDTO
import com.KY.KoreanYoutube.upload.Event
import com.KY.KoreanYoutube.user.UserRepository
import jakarta.transaction.Transactional
import mu.KotlinLogging
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.job.FFmpegJob
import org.springframework.core.io.FileSystemResource
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.core.scheduler.Schedulers
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration
import java.util.*
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

@Service
class StreamService(
    private val streamRepository: StreamRepository,
    private val userRepository: UserRepository,
    private val ffmpeg: FFmpeg,
    private val ffprobe: FFprobe,
) {
    val logger = KotlinLogging.logger {  }
    val HLS_BASE_URL ="http://localhost:8080/api/v1/stream/ts/"

    val sink = Sinks.many().multicast().onBackpressureBuffer<Event>()

    @Transactional
    fun saveStream(streamDTO: StreamDTO): ResponseEntity<Any> {
        try {
            val user = checkNotNull(userRepository.findByIdOrNull(streamDTO.userId))
            if (user.onAir) {
                throw IllegalStateException()
            }
            val streamKey = UUID.randomUUID().toString()
            val stream = LiveStream(streamDTO, streamKey)
            user.onAir = true//나중에 요청으로 변경
            streamRepository.save(stream)
            return ResponseEntity(streamKey, OK)
        } catch (e: Exception) {
            return ResponseEntity(BAD_REQUEST)
        }

    }

    fun startStream(key: String): Flux<ServerSentEvent<String>> {
        val streamPath = Paths.get("stream")

        val filePath = "/tmp/hls/$key"
        val m3u8Path = Paths.get(File.separatorChar+"tmp",File.separatorChar + "hls", File.separatorChar + key,File.separatorChar + "index.m3u8" )

        //Flux.merge(checkStreamStart(m3u8Path),startStreamListen(streamPath, key, m3u8Path, filePath)).subscribeOn(Schedulers.parallel()).subscribe()
        //Flux.merge(startStream,checkStreamStart).subscribe()
        checkStreamStart(m3u8Path).subscribeOn(Schedulers.parallel()).subscribe()
        logger.info { "middle point" }
        return sink.asFlux().map { event ->
            ServerSentEvent.builder<String>(event.message)
                .event(event.event)
                .build()
        }
    }

    private fun checkStreamStart(m3u8Path: Path): Flux<Boolean> {
        logger.info { "checking" }
        return Flux.interval(Duration.ofSeconds(1))
            .take(600)
            .map {
                logger.info { m3u8Path }
                if (m3u8Path.exists()) {
                    logger.info { "finish" }
                    sink.tryEmitNext(Event("finish", "finish"))
                    return@map true
                }
                else{
                    return@map false
                }
            }
            .takeUntil{it == true}
    }

    private fun startStreamListen(
        streamPath: Path,
        key: String,
        m3u8Path: String,
        filePath: String
    ): Mono<FFmpegJob> {
        return Mono.just(streamPath).doOnNext {
            if (!streamPath.isDirectory()) {
                Files.createDirectory(Paths.get("stream"))
            }
        }.map {
            Paths.get("stream/$key")
        }
        .doOnNext { keyPath->
            if (!keyPath.isDirectory()) {
                Files.createDirectory(keyPath)
            }
        }.map{
            val builder = FFmpegBuilder()
                .addExtraArgs("-listen", "1")
                .setInput("rtmp://localhost:1935/live/$key")
                .addExtraArgs("-timeout", "600")
                .addOutput(m3u8Path)
                .addExtraArgs("-c", "copy")
                .addExtraArgs("-bsf:v", "h264_mp4toannexb")
                .addExtraArgs("-hls_segment_filename", "${filePath}/${key}_%04d.ts")
                .addExtraArgs("-start_number", "0")
                .addExtraArgs("-hls_time", "5")
                .addExtraArgs("-hls_list_size", "0")
                .addExtraArgs("-hls_base_url", "$HLS_BASE_URL$key/")
                .addExtraArgs("-f", "hls")
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL).done()
                logger.info { "job실행" }
            FFmpegExecutor(ffmpeg, ffprobe).createJob(builder)
        }
            .doOnNext {job->
                job.run()
            }



    }

    fun getTsFile(key: String, fileName: String): ResponseEntity<Any> {
        //val path = Paths.get("stream",File.separatorChar + key, File.separatorChar + fileName)
        val path = Paths.get(File.separatorChar+"tmp",File.separatorChar + "hls", File.separatorChar + key,File.separatorChar + fileName )
        println(path.toString())

        return if(!path.exists()){
            ResponseEntity.badRequest().build()
        } else{
            val headers = HttpHeaders()
            val resource = FileSystemResource(path)
            headers.add("Content-Type", Files.probeContentType(path))
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            headers.add("Content-Transfer-Encoding", "binary;")
            ResponseEntity.ok().headers(headers).body(resource)
        }


    }
}