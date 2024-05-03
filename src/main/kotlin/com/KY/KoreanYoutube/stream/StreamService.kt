package com.KY.KoreanYoutube.stream

import com.KY.KoreanYoutube.domain.LiveStream
import com.KY.KoreanYoutube.dto.EventDTO
import com.KY.KoreanYoutube.dto.StreamDTO
import com.KY.KoreanYoutube.redis.RedisService
import com.KY.KoreanYoutube.user.UserService
import mu.KotlinLogging
import net.bramp.ffmpeg.FFmpeg
import net.bramp.ffmpeg.FFmpegExecutor
import net.bramp.ffmpeg.FFprobe
import net.bramp.ffmpeg.builder.FFmpegBuilder
import net.bramp.ffmpeg.job.FFmpegJob
import org.springframework.core.io.FileSystemResource
import org.springframework.data.domain.Sort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono
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
    private val userService: UserService,
    private val ffmpeg: FFmpeg,
    private val ffprobe: FFprobe,
    private val redisService: RedisService
) {
    val logger = KotlinLogging.logger {  }
    val HLS_BASE_URL ="http://localhost:8080/api/v1/stream/ts/"

    val sink = Sinks.many().multicast().onBackpressureBuffer<EventDTO>()

    @Transactional(value = "transactionManager")
    fun saveStream(streamDTO: StreamDTO,userId: String): ResponseEntity<Any> {
        try {
            val user = checkNotNull(userService.findByUserId(userId))
            if (user.onAir) {
                throw IllegalStateException()
            }
            val streamKey = UUID.randomUUID().toString()
            val stream = LiveStream(streamDTO,userId,streamKey)
            streamRepository.save(stream)
            return ResponseEntity(streamKey, OK)
        } catch (e: Exception) {
            return ResponseEntity(BAD_REQUEST)
        }

    }

    @Transactional("transactionManager")
    fun startStream(key: String): Flux<ServerSentEvent<String>> {
        val stream = checkNotNull(streamRepository.findFirstByStreamKey(key))
        redisService.saveRtmp(key)
        userService.findByUserId(stream.userName)
            .toMono()
            .doOnNext {user ->
            user.onAir = true
            }
            .flatMapMany {
                checkStreamStart(key).subscribeOn(Schedulers.parallel())
            }
            .subscribe()


        logger.info { "middle point" }
        return sink.asFlux().map { event ->
            ServerSentEvent.builder<String>(event.message)
                .event(event.event)
                .build()
        }
    }

    private fun checkStreamStart(key: String): Flux<Boolean> {
        logger.info { "checking" }
        return Flux.interval(Duration.ofSeconds(1))
            .take(600)
            .map {
                logger.info { redisService.loadRtmp(key) }
                if (redisService.loadRtmp(key) ==null) {
                    logger.info { "finish" }
                    redisService.saveRtmpIng(key)
                    sink.tryEmitNext(EventDTO("finish", "finish"))
                    return@map true
                }
                else{
                    return@map false
                }
            }
            .takeUntil{it == true}
    }


    fun getTsFile(key: String, fileName: String): ResponseEntity<Any> {
        val path = Paths.get(File.separatorChar+"tmp",File.separatorChar + "hls", File.separatorChar + key,File.separatorChar + fileName )
        println(path.toString())

        return if(!path.exists()){
            ResponseEntity.badRequest().build()
        } else{
            val headers = HttpHeaders()
            val resource = FileSystemResource(path)
            headers.add("Content-Type", Files.probeContentType(path))
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.filename + "\"")
            headers.add("Content-Transfer-Encoding", "binary;")
            ResponseEntity.ok().headers(headers).body(resource)
        }


    }

    fun findAllOnAir(): List<LiveStream> {
        return streamRepository.findByOnAirIsTrueOrderByCreateDateDesc()
    }

    fun verifyStream(streamKey: String) : ResponseEntity<HttpStatus> {
        logger.info { "Verify" }
        return if(redisService.loadRtmpAndRemove(streamKey) ==null){
            ResponseEntity.badRequest().build()
        } else{
            ResponseEntity.ok().build()
        }
    }


    @Transactional("transactionManager")
    fun doneStream(streamKey: String) : ResponseEntity<HttpStatus> {
        logger.info { "Done" }
        try{
            redisService.doneRtmpIng(streamKey)
            val stream = checkNotNull(streamRepository.findFirstByStreamKey(streamKey))
            stream.onAir = false
            val user = checkNotNull(userService.findByUserId(stream.userName))
            user.onAir = false
            return ResponseEntity.ok().build()
        } catch (e : Exception){
            return ResponseEntity.badRequest().build()
        }
    }

    fun findByStreamKey(streamKey : String): LiveStream? {
        return streamRepository.findFirstByStreamKey(streamKey)
    }
}