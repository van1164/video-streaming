package com.van1164.common.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class RedisRepository(private val redisTemplate: RedisTemplate<String, String>,
                      private val mapper: ObjectMapper) {

    //val redisTemplate by lazy { RedisConfig().redisTemplate() }

    fun save(key : String, value : String, duration: Duration){
        redisTemplate.opsForValue().set(key,value, duration)
    }

    fun load(key:String): String? {
        return redisTemplate.opsForValue().get(key)
    }

    fun <T> load(key : String, type : Class<T>): T? {
        return mapper.readValue(redisTemplate.opsForValue().get(key), type)
    }

    fun removeRtmp(streamKey: String): String? {
        return redisTemplate.opsForValue().getAndDelete(streamKey)
    }

}