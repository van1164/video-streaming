package com.van1164.video
import com.van1164.security.PrincipalDetails
import com.van1164.common.util.Utils.logger
import com.van1164.common.dto.UploadVideoDataDTO
import com.van1164.common.dto.UploadVideoPartDTO
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.ServerSentEvent
import org.springframework.http.codec.multipart.FilePart
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
) {


    @PostMapping("/test")
    //@PreAuthorize("isAuthenticated()")
    @ResponseBody
    fun testVideo(
        @RequestPart(value = "video") video: MultipartFile,
        @RequestPart(value = "data") dto: UploadVideoPartDTO,
    ): Mono<UploadVideoPartDTO> {
        return Mono.just(dto)
    }

    @PostMapping("/videoPart")
    //@PreAuthorize("isAuthenticated()")
    fun uploadVideoPart(
        @RequestPart video: FilePart,
        @RequestPart title : String,
        @RequestPart chunkNumber : String,
        @RequestPart totalChunk : String,
        @RequestPart fileUUID : String): Mono<ResponseEntity<HttpStatus>> {
        println("FFFFFFFFFFFFFFFFFFFFFFFF$chunkNumber")
        logger.info { "TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT" }
        return Mono.just(UploadVideoPartDTO(title,chunkNumber.toInt(),totalChunk.toInt(),fileUUID))
            .doOnNext {
                logger.info{ "여기에 있어요!!!"}
                logger.info { video.filename() }
            }
            .flatMap {videoData->
                videoService.uploadVideoPart(video, videoData)
            }
            .doOnError {
                logger.error { "에러" }
            }
            .onErrorReturn (
                ResponseEntity.badRequest().build()
                )
//        val videoData = UploadVideoPartDTO(title,chunkNumber,totalChunk,fileUUID)
//        return videoService.uploadVideoPart(video, videoData)


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
        logger.info { uploadVideoDataDTO.title }
        logger.info { uploadVideoDataDTO.fileUUID }
        logger.info { "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" }
        return videoService.saveVideoData(uploadVideoDataDTO.title,uploadVideoDataDTO.fileUUID,user.name)
    }

    //@PreAuthorize("isAuthenticated()")
    @GetMapping("/uploadPage")
    fun uploadPage() : Mono<String> {
        logger.info { "업로드 페이지" }
        return Mono.just("uploadPage")
    }


}