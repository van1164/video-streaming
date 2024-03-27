package com.KY.KoreanYoutube.upload

import com.KY.KoreanYoutube.upload.dto.UploadVideoPartDTO
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
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
            fileUUID : String): ResponseEntity<Any> {

        val videoData = UploadVideoPartDTO(title,chunkNumber,totalChunk,fileUUID)
        return uploadService.uploadVideoPart(video, videoData)


    }
    @ResponseBody
    @PostMapping("/videoPartLast")
     fun uploadVideoLast(
        title : String,
        chunkNumber : Int,
        totalChunk : Int,
        fileUUID : String
     ): String {
        val videoData = UploadVideoPartDTO(title,chunkNumber,totalChunk,fileUUID)
        return uploadService.uploadVideoPartLast(videoData)


    }
    @RequestMapping("/uploadPage")
    fun uploadPage() : String{
        return "uploadPage"
    }


}