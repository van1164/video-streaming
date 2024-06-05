package com.van1164.video.view.controller

import com.van1164.common.security.PrincipalDetails
import com.van1164.video.view.service.VideoViewService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/video/view")
class VideoViewController(
    private val videoViewService: VideoViewService,
) {

    @PostMapping("/{videoId}")
    fun videoLike(
        @PathVariable("videoId") videoId: Long,
        @AuthenticationPrincipal principal: Mono<PrincipalDetails>
    ): Mono<ResponseEntity<Long>> {
        return principal.flatMap { user ->
            videoViewService.videoView(user.name, videoId)
        }
            .onErrorReturn(ResponseEntity.badRequest().build())
    }
}