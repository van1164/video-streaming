package com.van1164.security

import ch.qos.logback.classic.PatternLayout.HEADER_PREFIX
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono
import java.util.concurrent.Callable
import java.util.function.Function


@Component
class JwtAuthenticationFilterReactive(
    private val jwtTokenProvider: JwtTokenProvider,
) : WebFilter {

    private fun resolveToken(request: ServerHttpRequest): String? {
        val bearerToken = request.headers.getFirst("authorization")
        if (StringUtils.hasText(bearerToken) && bearerToken!!.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return null
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val token = resolveToken(exchange.request)
        if (StringUtils.hasText(token) && token !=null &&jwtTokenProvider.validateToken(token)) {
            return jwtTokenProvider.getAuthentication(token)
                .flatMap{ authentication ->
                    println(authentication)
                    SecurityContextHolder.getContext().authentication = authentication
                    ReactiveSecurityContextHolder.withAuthentication(authentication).toMono().flatMap {
                        println(it.toString())
                        chain.filter(exchange)
                    }
                }
        }
        return chain.filter(exchange)
    }


}