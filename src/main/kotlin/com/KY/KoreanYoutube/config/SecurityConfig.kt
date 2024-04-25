package com.KY.KoreanYoutube.config

import com.KY.KoreanYoutube.security.JwtAuthenticationFilter
import com.KY.KoreanYoutube.security_reactive.JwtAuthenticationFilterReactive
import com.KY.KoreanYoutube.security_reactive.OAuthSuccessHandlerReactive
import com.KY.KoreanYoutube.security_reactive.PrincipalOauthUserServiceReactive
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher


val log = KotlinLogging.logger{}


@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
class SecurityConfig(
    val principalOauthUserService: PrincipalOauthUserServiceReactive,
    val oAuthSuccessHandler: OAuthSuccessHandlerReactive,
    val jwtAuthenticationFilter: JwtAuthenticationFilterReactive,
    val clientRegistrationRepository: ReactiveClientRegistrationRepository
) {
    @Bean
    fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.httpBasic {
            it.disable()
            }
            .csrf {
                it.disable()
            }
            .authorizeExchange{
                it.pathMatchers("/api/v1/stream/done").permitAll()
                it.pathMatchers("/api/v1/stream/verify").permitAll()
                it.pathMatchers("/api/v1/stream/live/**") .permitAll()
                it.pathMatchers("/api/v1/stream/**").authenticated()
                it.pathMatchers("/api/v1/upload/**").authenticated()
                it.pathMatchers("/**").permitAll()
            }
            .exceptionHandling{
                it.authenticationEntryPoint(RedirectServerAuthenticationEntryPoint("/loginPage"))
            }
            .oauth2Login {
                it.authenticationSuccessHandler(oAuthSuccessHandler)
                it.authorizationRequestResolver(authorizationRequestResolver())
            }
            .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        return http.build()
    }
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    private fun authorizationRequestResolver(): ServerOAuth2AuthorizationRequestResolver {
        val authorizationRequestMatcher: ServerWebExchangeMatcher = PathPatternParserServerWebExchangeMatcher(
            "/oauth2/authorization/{registrationId}"
        )

        return DefaultServerOAuth2AuthorizationRequestResolver(
            clientRegistrationRepository, authorizationRequestMatcher
        )
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
