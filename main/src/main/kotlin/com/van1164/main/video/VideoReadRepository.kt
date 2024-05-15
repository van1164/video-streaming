package com.van1164.main.video

import com.van1164.common.domain.VideoR2dbc
import jakarta.persistence.QueryHint
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface VideoReadRepository : R2dbcRepository<VideoR2dbc,Long> {

    @QueryHints(value = [QueryHint(name = "org.hibernate.readOnly", value = "true")])
    override fun findAll(): Flux<VideoR2dbc>

}