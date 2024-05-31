package com.van1164.security.config


import com.van1164.security.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
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
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher

@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
@ComponentScan(basePackages = ["com.van1164.user","com.van1164.common"])
class SecurityConfig(
    val oAuthSuccessHandler: OAuthSuccessHandlerReactive,
    val jwtAuthenticationFilter: JwtAuthenticationFilterReactive,
    val clientRegistrationRepository: ReactiveClientRegistrationRepository,
    val authenticationEntryPoint: CustomAuthenticationEntryPoint,
    authManager : JwtAuthenticationManager,
    converter: JwtServerAuthenticationConverter
) {
    val filter = AuthenticationWebFilter(authManager).apply{
        this.setServerAuthenticationConverter(converter)
    }

    @Bean
    fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.httpBasic {
            it.disable()
            }
            .csrf {
                it.disable()
            }
            .authorizeExchange{
                it.pathMatchers("/api/v1/comment/").permitAll()
                it.pathMatchers("/api/v1/stream/done").permitAll()
                it.pathMatchers("/api/v1/stream/verify").permitAll()
                it.pathMatchers("/api/v1/stream/live/**") .permitAll()
                it.pathMatchers("/api/v1/stream/**").authenticated()
                it.pathMatchers("/api/v1/upload/videoPart").permitAll()
                it.pathMatchers("/api/v1/upload/test").permitAll()
                it.pathMatchers("/api/v1/upload/**").authenticated()
                it.pathMatchers("/**").permitAll()
            }
            .exceptionHandling{
                it.authenticationEntryPoint(authenticationEntryPoint)
            }
            .oauth2Login {
                it.authenticationSuccessHandler(oAuthSuccessHandler)
                it.authorizationRequestResolver(authorizationRequestResolver())
            }
            .addFilterBefore(filter, SecurityWebFiltersOrder.AUTHENTICATION)
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

}
