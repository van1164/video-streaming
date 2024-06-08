package com.van1164.main_module.video

import com.van1164.common.domain.VideoR2dbc
import com.van1164.main_module.video.VideoReadRepository
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Service
@EnableR2dbcRepositories(basePackageClasses = [VideoReadRepository::class])
class VideoReadService(
    val videoRepository: VideoReadRepository
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