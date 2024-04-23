package com.KY.KoreanYoutube.stream

import com.KY.KoreanYoutube.dto.StreamDTO
import com.KY.KoreanYoutube.redis.RedisService
import com.KY.KoreanYoutube.security.PrincipalDetails
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.ServerSentEvent
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux


@Controller
@RequestMapping("/api/v1/stream")
class StreamController(
    val streamService: StreamService,
) {
    val logger = KotlinLogging.logger{}
    @GetMapping("/streamPage")
    fun streamPage(): String {
        return "streaming"
    }

    @ResponseBody
    @GetMapping("start_stream/{key}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun startStream(
        @PathVariable key : String,
    ): Flux<ServerSentEvent<String>> {
        return streamService.startStream(key)
    }



    @PostMapping("/save_stream")
    fun saveStream(
        @AuthenticationPrincipal user : PrincipalDetails,
        streamDTO : StreamDTO
    ): ResponseEntity<Any> {
        return streamService.saveStream(streamDTO,user.name)
    }

    @ResponseBody
    @GetMapping("/live/{key}")
    fun getm3u8(
        @PathVariable("key") key : String,
    ): ResponseEntity<Any> {
        val fileName = "index.m3u8"
        logger.info { fileName }
        return streamService.getTsFile(key,fileName)
    }

    @ResponseBody
    @GetMapping("/ts/{key}/{filename}")
    fun getTs(
        @PathVariable("key") key : String,
        @PathVariable("filename") fileName : String,
    ): ResponseEntity<Any> {
        logger.info { fileName }
        return streamService.getTsFile(key,fileName)
    }

    @GetMapping("/verify")
    fun verify(@RequestParam name : String): ResponseEntity<HttpStatus> {
        return streamService.verifyStream(name)
    }

    @GetMapping("/done")
    fun done(@RequestParam name : String): ResponseEntity<HttpStatus> {
        return streamService.doneStream(name)
    }
}