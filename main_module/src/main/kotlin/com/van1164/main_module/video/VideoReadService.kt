package com.van1164.main_module.video

import com.van1164.common.domain.VideoR2dbc
import com.van1164.video.video.VideoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
class VideoReadService(
    val videoRepository: VideoRepository
) {

    @Transactional(readOnly = true)
    fun findAllSortByDescending(): Flux<VideoR2dbc> {
        return videoRepository.findAllByOrderByCreatedDateDesc()
    }

    @Transactional(readOnly = true)
    fun findByUrl(url : String): Mono<VideoR2dbc> {
        return videoRepository.findByUrl(url)
    }
}