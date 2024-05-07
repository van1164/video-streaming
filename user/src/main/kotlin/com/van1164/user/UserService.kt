package com.van1164.user

import com.van1164.common.domain.UserR2dbc
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
@EnableR2dbcRepositories(basePackageClasses = [UserRepository::class])
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