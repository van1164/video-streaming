package com.van1164.video.video

import com.van1164.common.domain.VideoR2dbc
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/api/v1/video")
class VideoController(
    private val videoService: VideoService,
) {

    @GetMapping("/main_list")
    fun mainList(
        @RequestParam(required = false, value = "cursorId") cursorId :Long?,
        @RequestParam(required = false, value = "size", defaultValue = "10") size :Int
    ): Flux<VideoR2dbc> {
        return videoService.getMainPage(cursorId,size)
    }

}