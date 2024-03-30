package com.KY.KoreanYoutube.upload

import com.KY.KoreanYoutube.upload.dto.UploadVideoPartDTO
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture


@Controller
@RequestMapping("/api/v1/upload")
class UploadController(
    val uploadService: UploadService
) {

    @PostMapping("/video")
    fun uploadVideo(@RequestPart(name = "video") video: MultipartFile,
                    @RequestPart(name = "videoData")videoData: UploadVideoPartDTO): ResponseEntity<Any> {

        return uploadService.uploadVideo(video, videoData)


    }

    @PostMapping("/videoPart")
    fun uploadVideoPart(@RequestPart(name = "video") video: MultipartFile,
            title : String,
            chunkNumber : Int,
            totalChunk : Int,
            fileUUID : String): Mono<ResponseEntity<HttpStatus>> {

        val videoData = UploadVideoPartDTO(title,chunkNumber,totalChunk,fileUUID)
        return uploadService.uploadVideoPart(video, videoData)


    }
    @ResponseBody
    @GetMapping("/videoPartLast/{id}/{totalChunk}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
     fun uploadVideoLast(
        @PathVariable id : String,
        @PathVariable totalChunk: Int,
     ): Flux<ServerSentEvent<String>> {
        return uploadService.uploadVideoPartLast(id,totalChunk)


    }

    @PostMapping("/saveVideoData")
    fun saveVideoData(
        title: String,
        fileUUID: String,
    ){
        uploadService.saveVideoData(title,fileUUID)
    }

    @RequestMapping("/uploadPage")
    fun uploadPage() : String{
        return "uploadPage"
    }


}