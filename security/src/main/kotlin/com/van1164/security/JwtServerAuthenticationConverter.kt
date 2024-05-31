package com.van1164.security

import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtServerAuthenticationConverter(
    val jwtTokenProvider: JwtTokenProvider
) : ServerAuthenticationConverter {
    override fun convert(exchange: ServerWebExchange?): Mono<Authentication> {
        return Mono.justOrEmpty(exchange?.request?.headers?.getFirst("authorization"))
            .filter { it.startsWith("Bearer ") }.map { it.substring((7)) }
            .filter { jwtTokenProvider.validateToken(it) }
            .flatMap { jwt -> jwtTokenProvider.getAuthentication(jwt) }
    }
}
