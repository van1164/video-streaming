package com.KY.KoreanYoutube.security

import com.KY.KoreanYoutube.domain.User
import com.KY.KoreanYoutube.redis.RedisRepository
import com.KY.KoreanYoutube.user.UserRepository
import com.KY.KoreanYoutube.user.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import mu.KotlinLogging
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

val logger = KotlinLogging.logger { }

@Component(value = "authenticationSuccessHandler")
class OAuthSuccessHandler(
    val userService: UserService,
    val redisRepository: RedisRepository,
    val jwtTokenProvider: JwtTokenProvider,
    val userRepository: UserRepository
) : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        println("TTTTTTTTTTTTTTTTTTTTTTTTTT")
        val principal = authentication.principal as PrincipalDetails
        val userName = principal.getProvider().name +"_"+ principal.getEmail()
        val jwt = jwtTokenProvider.createToken(userName)
        val user = userRepository.findUserByName(userName)
        redisRepository.save(jwt.token,user!!)
        response.status = HttpServletResponse.SC_OK
        response.contentType = "application/json;charset=UTF-8"
        response.addHeader("Authorization", jwt.token)
        response.sendRedirect("http://localhost:8080/access/google?code=" + jwt.token)
    }
}