package com.van1164.user

import com.van1164.common.domain.UserR2dbc
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository : R2dbcRepository<UserR2dbc,Long> {
    fun findFirstByUserId(userId: String): Mono<UserR2dbc>
}