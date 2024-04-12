package com.KY.KoreanYoutube.redis

import com.KY.KoreanYoutube.domain.User
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class RedisRepository(val redisTemplate: RedisTemplate<String, String>, val mapper: ObjectMapper) {

    //val redisTemplate by lazy { RedisConfig().redisTemplate() }

    fun save(jwt: String, user: User) {
        redisTemplate.opsForValue().set(jwt, mapper.writeValueAsString(user), Duration.ofHours(5))
    }

    fun loadByJwt(jwt: String): User? {
        return mapper.readValue(redisTemplate.opsForValue().get(jwt),User::class.java)
    }

}