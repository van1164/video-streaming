package com.van1164.main_module.live

import com.van1164.common.domain.LiveStream
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface LiveReadRepository : R2dbcRepository<LiveStream,Long>{
    fun findByOnAirIsTrueOrderByCreatedDateDesc(): Flux<LiveStream>
}