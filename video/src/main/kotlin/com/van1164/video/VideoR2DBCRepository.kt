package com.van1164.video

import com.van1164.common.domain.VideoR2dbc
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface VideoR2DBCRepository : R2dbcRepository<VideoR2dbc,String> {
    fun findFirstByUrl(url: String):Mono<VideoR2dbc>

}