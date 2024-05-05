package com.van1164.security


import com.van1164.common.dto.*
import com.van1164.common.domain.UserR2dbc
import com.van1164.common.util.Utils.logger
import com.van1164.user.UserService
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty


@Service
class PrincipalOauthUserServiceReactive(
    private val userService: UserService,
) : ReactiveOAuth2UserService<OAuth2UserRequest,OAuth2User> {
    //구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
    //함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Throws(OAuth2AuthenticationException::class)
    @Transactional
    override fun loadUser(userRequest: OAuth2UserRequest): Mono<OAuth2User> {

        return oAuthToUser(userRequest)

    }

    private fun oAuthToUser(userRequest: OAuth2UserRequest): Mono<OAuth2User> {
        val delegate = DefaultReactiveOAuth2UserService()
        return delegate.loadUser(userRequest)
            .doOnNext {
                logger.info { "PRINCIPAL 지나감" }
            }
            .map { oAuth2User ->
                var oAuth2UserInfo: OAuth2UserInfo? = null
                when (userRequest.clientRegistration.registrationId) {
                    "google" -> {
                        oAuth2UserInfo = GoogleUserInfo(oAuth2User.attributes)
                    }

                    "naver" -> {
                        oAuth2UserInfo = NaverUserInfo(oAuth2User.attributes["response"] as Map<String, Any>)
                    }

                    "kakao" -> {
                        oAuth2UserInfo = KakaoUserInfo(
                            oAuth2User.attributes["kakao_account"] as Map<String, Any>,
                            oAuth2User.attributes["id"].toString()
                        )
                    }

                    else -> {
                        println("지원하지 않은 로그인 서비스 입니다.")
                    }
                }
                Pair(oAuth2UserInfo!!, oAuth2User.attributes)
            }
            .flatMap { p ->
                Mono.zip(userInfo2User(p.first), Mono.just(p.second))
            }
            .map { p ->
                PrincipalDetails(p.t1, p.t2)
            }
    }

    private fun userInfo2User(oAuth2UserInfo : OAuth2UserInfo): Mono<UserR2dbc> {
        val provider: OAuthProvider = oAuth2UserInfo.provider
        val providerId: String = oAuth2UserInfo.providerId
        val password = providerId //중요하지 않음 그냥 패스워드 암호화 하
        val email: String = oAuth2UserInfo.email
        val userId = provider.name + "_" + email
        val role: Role = Role.USER
        val name = oAuth2UserInfo.name

        return userService.findByUserId(userId).switchIfEmpty{
                    val user = UserR2dbc(
                        name = name,
                        password = password,
                        email = email,
                        role = role,
                        provider = provider,
                        userId = userId
                    )
                    userService.save(user)
            }
    }
}