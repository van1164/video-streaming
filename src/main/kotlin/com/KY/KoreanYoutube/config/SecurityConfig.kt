package com.KY.KoreanYoutube.config

import com.KY.KoreanYoutube.security.JwtAuthenticationFilter
import com.KY.KoreanYoutube.security.OAuthSuccessHandler
import com.KY.KoreanYoutube.security.PrincipalOauthUserService
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository


val log = KotlinLogging.logger{}

@EnableWebSecurity
@Configuration
class SecurityConfig(
    val principalOauthUserService: PrincipalOauthUserService,
    val oAuthSuccessHandler: OAuthSuccessHandler,
    val jwtAuthenticationFilter: JwtAuthenticationFilter
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http { // kotlin DSL
            httpBasic { disable() }
            csrf { disable() }
            cors { }
            authorizeRequests {
                authorize("/api/v1/upload/**",authenticated)
                authorize("/**",permitAll)
            }
            oauth2Login {
                loginPage = "/loginPage"
                defaultSuccessUrl("/",true)
                userInfoEndpoint {
                    userService = principalOauthUserService
                }
                authenticationSuccessHandler = oAuthSuccessHandler
            }

            exceptionHandling {
                //authenticationEntryPoint = serverAuthenticationEntryPoint()
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)


        }
        return http.build()
    }
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
//    private fun serverAuthenticationEntryPoint(): AuthenticationEntryPoint? {
//        return AuthenticationEntryPoint { request,response, authEx: AuthenticationException ->
//            val requestPath = request.pathInfo
//            log.error("Unauthorized error: {}", authEx.message)
//            log.error("Requested path    : {}", requestPath)
//
//            val serverHttpResponse: HttpServletResponse = response
//            serverHttpResponse.contentType = MediaType.APPLICATION_JSON.toString()
//            serverHttpResponse.status = HttpStatus.UNAUTHORIZED.value()
//
//            val errorMessage= ErrorMessage(
//                HttpStatus.UNAUTHORIZED.value(),
//                LocalDateTime.now(),
//                authEx.message,
//                requestPath
//            )
//            try {
//                val errorByte = ObjectMapper()
//                    .registerModule(JavaTimeModule())
//                    .writeValueAsBytes(errorMessage)
//                val dataBuffer: DataBuffer = serverHttpResponse
//                return@AuthenticationEntryPoint serverHttpResponse.writer.write(
//                    Mono.just(
//                        dataBuffer
//                    )
//                )
//            } catch (e: JsonProcessingException) {
//                log.error(e.message, e)
//                return@AuthenticationEntryPoint serverHttpResponse.
//            }
//        }
//    }
//
//
//}
//
//data class ErrorMessage(val value: Int, val now: LocalDateTime?, val message: String?, val requestPath: String) {
//
}
