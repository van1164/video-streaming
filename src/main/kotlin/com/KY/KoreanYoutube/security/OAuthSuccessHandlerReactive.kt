package com.KY.KoreanYoutube.security

import com.KY.KoreanYoutube.domain.User
import com.KY.KoreanYoutube.redis.RedisRepository
import com.KY.KoreanYoutube.user.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import mu.KotlinLogging
import org.apache.http.impl.client.DefaultRedirectStrategy
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.server.DefaultServerRedirectStrategy
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono


//@Component(value = "authenticationSuccessHandler")
//class OAuthSuccessHandlerReactive(
//    val userService: UserService,
//    val redisRepository: RedisRepository,
//    val jwtTokenProvider: JwtTokenProvider,
//) : ServerAuthenticationSuccessHandler {
//    override fun onAuthenticationSuccess(
//        webFilterExchange: WebFilterExchange,
//        authentication: Authentication
//    ): Mono<Void>? {
//        println("TTTTTTTTTTTTTTTTTTTTTTTTTT")
//        val principal = authentication.principal as PrincipalDetails
//        val userName = principal.getProvider().name +"_"+ principal.getEmail()
//        val jwt = jwtTokenProvider.createToken(userName)
//       return  userService.findUserByName(userName)
//            .doOnNext {user ->
//                redisRepository.save(jwt.token,user!!)
//            }
//            .flatMap {
//                val uri = UriComponentsBuilder.newInstance().scheme("http").host("localhost:8080").path("/access/google").queryParam("code",jwt.token).build().toUri()
//                val exchange = webFilterExchange.exchange
//                DefaultServerRedirectStrategy().sendRedirect(exchange,uri)
//            }
//
//
//    }
//}