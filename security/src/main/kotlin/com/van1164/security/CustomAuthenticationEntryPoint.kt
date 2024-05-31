package com.van1164.security

import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class CustomAuthenticationEntryPoint : ServerAuthenticationEntryPoint {
    override fun commence(exchange: ServerWebExchange, ex: AuthenticationException): Mono<Void> {
        return Mono.fromRunnable{ exchange.response.setStatusCode(HttpStatus.UNAUTHORIZED) }
    }

}