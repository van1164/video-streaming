package com.KY.KoreanYoutube.security

import com.KY.KoreanYoutube.redis.RedisService
import com.KY.KoreanYoutube.user.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.apache.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

val logger = KotlinLogging.logger { }

@Component
class OAuthSuccessHandler(
    val userService: UserService,
    val redisService: RedisService,
    val jwtTokenProvider: JwtTokenProvider,
) : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        println("TTTTTTTTTTTTTTTTTTTTTTTTTT")
        val principal = authentication.principal as PrincipalDetails
        val userId = principal.getProvider().name +"_"+ principal.getEmail()
        val jwt = jwtTokenProvider.createToken(userId)
        val user = userService.findByUserId(userId)
        if(user == null){
            response.sendRedirect("http://localhost:8080/?error")
        }
        else{
            redisService.saveJwt(jwt.token,user)
            response.status = HttpServletResponse.SC_OK
            response.contentType = "application/json;charset=UTF-8"
            response.addHeader(HttpHeaders.AUTHORIZATION, jwt.token)
            response.setHeader(HttpHeaders.AUTHORIZATION, jwt.token)
            response.sendRedirect("/?token="+jwt.token)
        }

    }
}