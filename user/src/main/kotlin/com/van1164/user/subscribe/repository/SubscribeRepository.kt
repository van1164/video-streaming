package com.van1164.user.subscribe.repository

import com.van1164.common.domain.UserSubscribe
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface SubscribeRepository : R2dbcRepository<UserSubscribe,Long> {

    fun existsByFromUserIdAndToUserId(fromUserId: String,toUserId: String) : Mono<Boolean>

    fun findByFromUserIdAndToUserId(fromUserId : String, toUserId : String) : Mono<UserSubscribe>
}