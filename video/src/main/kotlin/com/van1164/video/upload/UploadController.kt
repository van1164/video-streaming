package com.van1164.video.upload
import com.van1164.common.security.PrincipalDetails
import com.van1164.common.dto.UploadVideoDataDTO
import com.van1164.common.dto.UploadVideoPartDTO
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.ServerSentEvent
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
@RequestMapping("/api/v1/upload")
class UploadController(
    val uploadService: UploadService,
) {
    @ResponseBody
    @PostMapping("/videoPart")
    //@PreAuthorize("isAuthenticated()")
    fun uploadVideoPart(
        @RequestPart video: FilePart,
        @RequestPart title : String,
        @RequestPart chunkNumber : String,
        @RequestPart totalChunk : String,
        @RequestPart fileUUID : String): Mono<ResponseEntity<Boolean>> {
        return Mono.just(UploadVideoPartDTO(title,chunkNumber.toInt(),totalChunk.toInt(),fileUUID))
            .flatMap {videoData->
                uploadService.uploadVideoPart(video, videoData)
            }
            .onErrorReturn (
                ResponseEntity.badRequest().build()
                )
    }
    @ResponseBody
    @GetMapping("/videoPartLast/{id}/{totalChunk}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
     fun uploadVideoLast(
        @PathVariable id : String,
        @PathVariable totalChunk: Int,
     ): Flux<ServerSentEvent<String>> {
        return uploadService.uploadVideoPartLast(id,totalChunk)
    }


    @Operation(description = "동영상에 대한 정보를 저장함. uploadVideoLast와 같이 요청 보내야됨.")
    @ResponseBody
    @PostMapping("/saveVideoData")
    fun saveVideoData(
        @AuthenticationPrincipal principal : Mono<PrincipalDetails>,
        @RequestBody  uploadVideoDataDTO: UploadVideoDataDTO
    ): Mono<ResponseEntity<Boolean>> {
        return principal.flatMap { user->
            uploadService.saveVideoData(uploadVideoDataDTO.title,uploadVideoDataDTO.fileUUID,user.name)
        }
    }

    //@PreAuthorize("isAuthenticated()")
    @GetMapping("/uploadPage")
    fun uploadPage() : Mono<String> {
        return Mono.just("uploadPage")
    }

}