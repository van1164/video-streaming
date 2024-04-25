package com.KY.KoreanYoutube.user

import com.KY.KoreanYoutube.domain.User
import com.KY.KoreanYoutube.domain.UserR2dbc
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
class UserR2DBCService(
    val userR2DBCRepository: UserR2DBCRepository
) {

    fun findByUserId(userId : String): Mono<UserR2dbc> {
        return userR2DBCRepository.findFirstByUserId(userId)
    }

    fun save(user: UserR2dbc): Mono<UserR2dbc> {
        return userR2DBCRepository.save(user)
    }
}