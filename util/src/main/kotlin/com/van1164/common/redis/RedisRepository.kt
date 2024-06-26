package com.van1164.common.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Duration


@Repository
class RedisRepository(
    private val redisTemplate : ReactiveRedisTemplate<String,String>,
    private val mapper: ObjectMapper
) {

    fun remove(key : String): Mono<Long> {
        return redisTemplate.delete(key)
    }

    fun increment(key: String): Mono<Long> {
        return redisTemplate.opsForValue().increment(key)
    }

    fun decrement(key: String): Mono<Long> {
        return redisTemplate.opsForValue().decrement(key)
    }


    fun save(key : String, value : String, duration: Duration): Mono<Boolean> {
        return redisTemplate.opsForValue().set(key,value, duration)
    }

    fun load(key:String): Mono<String> {
        return redisTemplate.opsForValue().get(key)
    }

    fun <T> load(key : String, type : Class<T>): Mono<T> {
        return redisTemplate.opsForValue()
                            .get(key)
                            .map { value-> mapper.readValue(value,type) }
    }

    fun removeRtmp(streamKey: String): Mono<String> {
        return redisTemplate.opsForValue().getAndDelete(streamKey)
    }

}