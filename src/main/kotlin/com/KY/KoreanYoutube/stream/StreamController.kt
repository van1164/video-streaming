package com.KY.KoreanYoutube.stream

import com.KY.KoreanYoutube.dto.StreamDTO
import com.KY.KoreanYoutube.security.PrincipalDetails
import mu.KotlinLogging
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
    val streamService: StreamService
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
        streamDTO : StreamDTO // 나중에 jwt로 변경 예정
    ): ResponseEntity<Any> {
        return streamService.saveStream(streamDTO,user.name)
    }

    @GetMapping("/stop")
    fun streamStop(
        @RequestParam("user_id") userId : String // 나중에 jwt로 변경 예정
    ){

    }

//    @GetMapping("/live/{user_id}")
//    fun getStream(
//        @PathVariable("user_id") userId : String // 나중에 jwt로 변경 예정
//    ){
//
//    }

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

}