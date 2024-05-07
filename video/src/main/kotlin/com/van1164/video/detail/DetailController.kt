package com.van1164.video.detail

import com.van1164.video.VideoService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/detail")
class DetailController(
    @Value("\${aws.s3.bucketUrl}")
    val bucketUrl : String,
    val videoService: VideoService
) {

    @GetMapping("/{detail_id}")
    fun loadDetailPage(model:Model, @PathVariable(name = "detail_id") detailId : String): String {
//        videoService.findById(detailId)
//            .map {video->
//                VideoDetailResponseDTO(
//                    videoUrl = "https://video-stream-spring.s3.ap-northeast-2.amazonaws.com/${video.url}/${video.url}.m3u8",
//                    title = video.title,
//                    commentList = video.
//                )
//
//            }

        model.addAttribute("m3u8Url" , "$bucketUrl$detailId/$detailId.m3u8")
        return "videoDetail"
    }

    @GetMapping("/live/{live_id}")
    fun loadLivePage(model:Model, @PathVariable(name = "live_id") liveId : String): String {
        model.addAttribute("streamKey" , liveId)
        return "streamDetail"
    }

}