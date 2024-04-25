package com.KY.KoreanYoutube.user

import com.KY.KoreanYoutube.domain.UserR2dbc
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserR2DBCRepository : R2dbcRepository<UserR2dbc,Long> {
    fun findFirstByUserId(userId: String): Mono<UserR2dbc>
}