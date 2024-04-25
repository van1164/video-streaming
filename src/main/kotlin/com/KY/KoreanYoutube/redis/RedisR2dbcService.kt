package com.KY.KoreanYoutube.redis

import com.KY.KoreanYoutube.domain.User
import com.KY.KoreanYoutube.domain.UserR2dbc
import com.KY.KoreanYoutube.utils.JWT_PREFIX
import com.KY.KoreanYoutube.utils.RTMP_ING_PREFIX
import com.KY.KoreanYoutube.utils.RTMP_PREFIX
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration


@Service
class RedisR2dbcService (
    private val redisRepository: RedisR2dbcRepository,
    private val mapper: ObjectMapper
) {

    fun saveRtmp(streamKey : String): Mono<Boolean> {
        return redisRepository.save(RTMP_PREFIX + streamKey,"off", Duration.ofMinutes(10))
    }

    fun loadRtmp(streamKey: String): Mono<String> {
        return redisRepository.load(RTMP_PREFIX + streamKey)
    }

    fun loadRtmpAndRemove(streamKey: String): Mono<String> {
        return redisRepository.removeRtmp(RTMP_PREFIX + streamKey)
    }


    fun saveJwt(jwt: String, user: UserR2dbc): Mono<Boolean> {
        return redisRepository.save(JWT_PREFIX +jwt,mapper.writeValueAsString(user), Duration.ofHours(3))
    }

    fun loadByJwt(jwt: String): Mono<User> {
        return redisRepository.load(JWT_PREFIX +jwt, User::class.java)
    }

    fun saveRtmpIng(key: String): Mono<Boolean> {
        return redisRepository.save(RTMP_ING_PREFIX + key,"live", Duration.ofHours(12))
    }

    fun doneRtmpIng(streamKey: String): Mono<String> {
        return redisRepository.removeRtmp(RTMP_ING_PREFIX +streamKey)
    }



}