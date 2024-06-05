package com.van1164.main.live

import com.van1164.common.domain.LiveStream
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
@EnableR2dbcRepositories(basePackageClasses = [LiveReadRepository::class])
class LiveReadService(
    val liveRepository: LiveReadRepository
) {

    fun findAllOnAir(): Flux<LiveStream> {
        return liveRepository.findByOnAirIsTrueOrderByCreatedDateDesc()
    }
}