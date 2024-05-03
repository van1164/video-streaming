package com.KY.KoreanYoutube.video

import com.KY.KoreanYoutube.config.log
import com.KY.KoreanYoutube.dto.UploadVideoDataDTO
import com.KY.KoreanYoutube.dto.UploadVideoPartDTO
import com.KY.KoreanYoutube.security.PrincipalDetails
import com.KY.KoreanYoutube.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.ServerSentEvent
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Controller
@RequestMapping("/api/v1/upload")
class VideoController(
    val videoService: VideoService,
    val userService: UserService
) {


    @PostMapping("/videoPart")
    @PreAuthorize("isAuthenticated()")
    fun uploadVideoPart(@RequestPart(name = "video") video: MultipartFile,
            title : String,
            chunkNumber : Int,
            totalChunk : Int,
            fileUUID : String): Mono<ResponseEntity<HttpStatus>> {

        val videoData = UploadVideoPartDTO(title,chunkNumber,totalChunk,fileUUID)
        return videoService.uploadVideoPart(video, videoData)


    }
    @ResponseBody
    @GetMapping("/videoPartLast/{id}/{totalChunk}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
     fun uploadVideoLast(
        @PathVariable id : String,
        @PathVariable totalChunk: Int,
     ): Flux<ServerSentEvent<String>> {
        return videoService.uploadVideoPartLast(id,totalChunk)
    }



    @PostMapping("/saveVideoData")
    fun saveVideoData(
        @AuthenticationPrincipal user : PrincipalDetails,
        @RequestBody  uploadVideoDataDTO: UploadVideoDataDTO
    ): Mono<ResponseEntity<HttpStatus>> {
        println(user)
        log.info { uploadVideoDataDTO.title }
        log.info { uploadVideoDataDTO.fileUUID }
        log.info { "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" }
        return videoService.saveVideoData(uploadVideoDataDTO.title,uploadVideoDataDTO.fileUUID,user.name)
    }

    //@PreAuthorize("isAuthenticated()")
    @RequestMapping("/uploadPage")
    fun uploadPage() : Mono<String> {
        return Mono.just("uploadPage")
    }


}