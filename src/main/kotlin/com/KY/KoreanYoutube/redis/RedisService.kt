package com.KY.KoreanYoutube.redis

import com.KY.KoreanYoutube.domain.User
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import java.time.Duration


@Service
class RedisService(
    private val redisRepository: RedisRepository,
    private val mapper: ObjectMapper
) {

    fun saveRtmp(streamKey : String){
        redisRepository.save(streamKey,"",Duration.ofMinutes(10))
    }

    fun loadRtmp(streamKey: String): String? {
        return redisRepository.load(streamKey,String::class.java)
    }


    fun saveJwt(jwt: String, user: User) {
        redisRepository.save(jwt,mapper.writeValueAsString(user),Duration.ofHours(3))
    }

    fun loadByJwt(jwt: String): User? {
        return redisRepository.load(jwt, User::class.java)
    }

}