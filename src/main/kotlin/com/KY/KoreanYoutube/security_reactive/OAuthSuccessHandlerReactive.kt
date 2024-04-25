package com.KY.KoreanYoutube.security_reactive

import com.KY.KoreanYoutube.redis.RedisR2dbcService
import com.KY.KoreanYoutube.security.JwtTokenProvider
import com.KY.KoreanYoutube.security.PrincipalDetails
import com.KY.KoreanYoutube.security.logger
import com.KY.KoreanYoutube.user.UserR2DBCService
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.DefaultServerRedirectStrategy
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono


@Component(value = "authenticationSuccessHandler")
class OAuthSuccessHandlerReactive(
    val userService: UserR2DBCService,
    val redisService: RedisR2dbcService,
    val jwtTokenProvider: JwtTokenProvider,
) : ServerAuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        logger.info("TTTTTTTTTTTTTTTTTTTTTTTTTT")
        val principal = authentication.principal as PrincipalDetailsReactive
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
                val uri = UriComponentsBuilder.newInstance().path("/").queryParam("token",jwt.token).build().toUri()
                val exchange = webFilterExchange.exchange
                DefaultServerRedirectStrategy().sendRedirect(exchange,uri)
            }


    }
}