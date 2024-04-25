package com.KY.KoreanYoutube.video

import com.KY.KoreanYoutube.domain.VideoR2dbc
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class VideoService(
    val videoR2DBCRepository: VideoR2DBCRepository
) {

    fun findAll(sort : Sort): Flux<VideoR2dbc> {
        return videoR2DBCRepository.findAll(sort)
    }

    fun findById(detailId: String): Mono<VideoR2dbc> {
        return videoR2DBCRepository.findFirstByUrl(detailId)
    }
}