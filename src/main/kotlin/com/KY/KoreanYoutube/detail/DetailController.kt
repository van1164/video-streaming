package com.KY.KoreanYoutube.detail

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class DetailController {

    @GetMapping("detail/{detail_id}")
    fun loadDetailPage(model:Model, @PathVariable(name = "detail_id") detailId : String): String {
        model.addAttribute("m3u8Url" , "https://video-stream-spring.s3.ap-northeast-2.amazonaws.com/$detailId/$detailId.m3u8")
        return "videoDetail"
    }
}