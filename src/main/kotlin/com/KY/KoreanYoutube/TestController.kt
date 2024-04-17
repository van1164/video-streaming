package com.KY.KoreanYoutube

import com.KY.KoreanYoutube.domain.VideoR2dbc
import com.KY.KoreanYoutube.video.VideoR2DBCRepository
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import reactor.core.publisher.Mono


@Controller
class TestController(
    val videoR2DBCRepository: VideoR2DBCRepository
) {

    @RequestMapping("/test_r2")
    fun testR2(): Mono<VideoR2dbc> {
        val video = VideoR2dbc("Test","URL","name","test")
        return videoR2DBCRepository.save(video)
    }
}