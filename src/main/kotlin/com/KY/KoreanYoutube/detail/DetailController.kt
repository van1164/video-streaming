package com.KY.KoreanYoutube.detail

import com.KY.KoreanYoutube.security.logger
import com.KY.KoreanYoutube.stream.StreamService
import com.KY.KoreanYoutube.video.VideoService
import com.google.gson.Gson
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/detail")
class DetailController(
    val streamService : StreamService,
    val videoService: VideoService
) {

    @GetMapping("/{detail_id}")
    fun loadDetailPage(model:Model, @PathVariable(name = "detail_id") detailId : String): String {
        model.addAttribute("m3u8Url" , "https://video-stream-spring.s3.ap-northeast-2.amazonaws.com/$detailId/$detailId.m3u8")
        return "videoDetail"
    }

    @GetMapping("/live/{live_id}")
    fun loadLivePage(model:Model, @PathVariable(name = "live_id") liveId : String): String {
        model.addAttribute("streamKey" , liveId)
        return "streamDetail"
    }

    @ResponseBody
    @GetMapping("/live/{key}")
    fun getm3u8(
        @PathVariable("key") key : String,
    ): ResponseEntity<Any> {
        val fileName = "index.m3u8"
        logger.info { fileName }
        return streamService.getTsFile(key,fileName)
    }

}