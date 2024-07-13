package com.van1164.common.aspect

import mu.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDateTime

@Aspect
@Configuration
class LoggingAdvice {
    companion object {
        val logger = KotlinLogging.logger { }
    }

    @Around("@annotation(com.van1164.common.aspect.LoggingStopWatch)")
    fun transactionLog(joinPoint: ProceedingJoinPoint): Any? {
        val className = joinPoint.signature.declaringTypeName
        val startAt = LocalDateTime.now()
        logger.info("Start At : $startAt")

        val proceed = joinPoint.proceed() as Mono<*>


        return proceed.doOnSuccess {
            val endAt = LocalDateTime.now()
            logger.info("End At : $startAt")
            logger.info("$className : ${Duration.between(startAt, endAt).toMillis()}ms")
        }
    }

}