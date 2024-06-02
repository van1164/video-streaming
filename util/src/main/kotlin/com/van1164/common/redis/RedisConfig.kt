package com.van1164.common.redis

import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration(value = "redisConfig")
@EnableRedisRepositories
@RequiredArgsConstructor
@ComponentScan(basePackages = ["com.van1164.common.redis"])
class RedisConfig {

    @Value("\${spring.data.redis.host}")
    lateinit var myHost: String

    @Value("\${spring.data.redis.port}")
    var myPort: Int = 6379

    @Value("\${spring.data.redis.password}")
    lateinit var myPassword: String


    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val configuration = RedisStandaloneConfiguration().apply {
            hostName = myHost
            port = myPort
            password = RedisPassword.of(myPassword)
        }
        val lettuceConnectionFactory = LettuceConnectionFactory(configuration)
        lettuceConnectionFactory.start()
        return lettuceConnectionFactory
    }

    @Bean
    fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        val configuration = RedisStandaloneConfiguration().apply {
            hostName = myHost
            this.port = myPort
            password = RedisPassword.of(myPassword)
        }
        println(configuration.hostName)
        println(configuration.port)
        println(configuration.password)
        val lettuceConnectionFactory = LettuceConnectionFactory(configuration)
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