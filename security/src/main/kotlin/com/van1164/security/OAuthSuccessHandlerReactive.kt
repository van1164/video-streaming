package com.van1164.security

import com.van1164.common.redis.RedisR2dbcService
import com.van1164.common.security.PrincipalDetails
import com.van1164.common.util.Utils.logger
import com.van1164.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono


@Component(value = "authenticationSuccessHandler")
class OAuthSuccessHandlerReactive(
    val userService: UserService,
    val redisService: RedisR2dbcService,
    val jwtTokenProvider: JwtTokenProvider,
) : ServerAuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        logger.info("TTTTTTTTTTTTTTTTTTTTTTTTTT")
        val principal = authentication.principal as PrincipalDetails
        val userName = principal.getProvider().name +"_"+ principal.getEmail()
        val jwt = jwtTokenProvider.createToken(userName)
       return  userService.findByUserId(userName)
           .doOnNext {
               logger.info { "AAAAAAAAAAAAAAAAAAAA" }
           }
            .flatMap {user ->
                redisService.saveJwt(jwt.token,user!!)
            }
           .doOnNext {
               logger.info { "SSSSSSSSSSSSS$it" }
           }
           .filter { it == true }
           .doOnNext {
               logger.info { "여기는 success 헨들러" }
           }
            .flatMap {
                Mono.fromRunnable{
                    val uri = UriComponentsBuilder.newInstance().path("/").queryParam("token",jwt.token).build().toUri()
                    val exchange = webFilterExchange.exchange
                    exchange.response.statusCode = HttpStatus.FOUND
                    exchange.response.headers.location = uri
                }
//                DefaultServerRedirectStrategy().sendRedirect(exchange,uri)
            }


    }
}