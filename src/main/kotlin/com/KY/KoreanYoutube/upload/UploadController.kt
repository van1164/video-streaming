package com.KY.KoreanYoutube.upload

import com.KY.KoreanYoutube.upload.dto.UploadVideoPartDTO
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile


@Controller
@RequestMapping("/api/v1/upload")
class UploadController(
    val uploadService: UploadService
) {

    @PostMapping("/video")
    fun uploadVideo(@RequestPart(name = "video") video: MultipartFile,
                    @RequestPart(name = "videoData")videoData: UploadVideoPartDTO): ResponseEntity<Any> {

        return uploadService.uploadVideoPart(video, videoData)


    }

    @RequestMapping("/uploadPage")
    fun uploadPage() : String{
        return "uploadPage"
    }


}