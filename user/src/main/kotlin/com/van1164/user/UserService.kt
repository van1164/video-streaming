package com.van1164.user

import com.van1164.common.domain.UserR2dbc
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
class UserService(
    val userRepository: UserRepository
) {

    fun findByUserId(userId : String): Mono<UserR2dbc> {
        return userRepository.findFirstByUserId(userId)
    }

    fun save(user: UserR2dbc): Mono<UserR2dbc> {
        return userRepository.save(user)
    }
}