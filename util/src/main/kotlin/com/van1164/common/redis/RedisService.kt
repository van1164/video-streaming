package com.van1164.common.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.van1164.common.domain.UserR2dbc
import com.van1164.util.JWT_PREFIX
import com.van1164.util.RTMP_ING_PREFIX
import com.van1164.util.RTMP_PREFIX
import com.van1164.util.SUBSCRIBE_PREFIX
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration


@Service
class RedisService (
    private val redisRepository: RedisRepository,
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

    fun loadByJwt(jwt: String): Mono<UserR2dbc> {
        return redisRepository.load(JWT_PREFIX +jwt, UserR2dbc::class.java)
    }

    fun saveRtmpIng(key: String): Mono<Boolean> {
        return redisRepository.save(RTMP_ING_PREFIX + key,"live", Duration.ofHours(12))
    }

    fun doneRtmpIng(streamKey: String): Mono<String> {
        return redisRepository.removeRtmp(RTMP_ING_PREFIX +streamKey)
    }

    fun increaseSubscribe(userId:String): Mono<Long> {
        return redisRepository.increment(SUBSCRIBE_PREFIX + userId)
    }

    fun decreaseSubscribe(userId: String): Mono<Long> {
        return redisRepository.decrement(SUBSCRIBE_PREFIX + userId)
    }


}