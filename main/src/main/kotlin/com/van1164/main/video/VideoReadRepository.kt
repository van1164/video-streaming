package com.van1164.main.video

import com.van1164.common.domain.VideoR2dbc
import jakarta.persistence.QueryHint
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface VideoReadRepository : R2dbcRepository<VideoR2dbc, Long> {

//    @QueryHints(value = [QueryHint(name = "org.hibernate.readOnly", value = "true")])
//    override fun findAll(): Flux<VideoR2dbc>

    @QueryHints(value = [QueryHint(name = "org.hibernate.readOnly", value = "true")])
    fun findAllByOrderByCreatedDateDesc(): Flux<VideoR2dbc>

    @QueryHints(value = [QueryHint(name = "org.hibernate.readOnly", value = "true")])
    override fun findById(id: Long): Mono<VideoR2dbc>

    @QueryHints(value = [QueryHint(name = "org.hibernate.readOnly", value = "true")])
    fun findByUrl(url: String): Mono<VideoR2dbc>


}