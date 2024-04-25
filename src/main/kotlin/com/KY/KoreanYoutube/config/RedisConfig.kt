package com.KY.KoreanYoutube.config

import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializationContext.RedisSerializationContextBuilder
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration(value = "redisConfig")
@EnableRedisRepositories
@RequiredArgsConstructor
class RedisConfig {

    @Value("\${spring.data.redis.host}")
    lateinit var host: String

    @Value("\${spring.data.redis.port}")
    var port: Int = 6379


    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val lettuceConnectionFactory = LettuceConnectionFactory(host, port)
        lettuceConnectionFactory.start()
        return lettuceConnectionFactory
    }

    @Bean
    fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        val lettuceConnectionFactory = LettuceConnectionFactory(host, port)
        lettuceConnectionFactory.start()
        return lettuceConnectionFactory
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, String> {
        val redisTemplate = RedisTemplate<String, String>()
        redisTemplate.connectionFactory = redisConnectionFactory()
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = StringRedisSerializer()
        redisTemplate.afterPropertiesSet()
        return redisTemplate
    }

//    @Bean
//    fun reactiveRedisTemplate(): ReactiveRedisTemplate<String, String> {
//        val serializationContext =
//            RedisSerializationContext.newSerializationContext<String, String>()
//                .key(StringRedisSerializer())
//                .value(StringRedisSerializer())
//                .build()
//        return ReactiveRedisTemplate<String, String>(reactiveRedisConnectionFactory(), serializationContext)
//
//    }
}