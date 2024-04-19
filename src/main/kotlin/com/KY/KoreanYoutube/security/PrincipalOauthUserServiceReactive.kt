package com.KY.KoreanYoutube.security

import com.KY.KoreanYoutube.domain.User
import com.KY.KoreanYoutube.domain.UserR2dbc
import com.KY.KoreanYoutube.user.UserService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty


//@Service
//class PrincipalOauthUserServiceReactive(
//    private val userService: UserService,
//) : ReactiveOAuth2UserService<OAuth2UserRequest,OAuth2User> {
//
//    //구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
//    //함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
//    @Throws(OAuth2AuthenticationException::class)
//    @Transactional(value = "transactionManager")
//    override fun loadUser(userRequest: OAuth2UserRequest): Mono<OAuth2User> {
//        //"registraionId" 로 어떤 OAuth 로 로그인 했는지 확인 가능(google,naver등)
//        //구글 로그인 버튼 클릭 -> 구글 로그인창 -> 로그인 완료 -> code를 리턴(OAuth-Clien라이브러리가 받아줌) -> code를 통해서 AcssToken요청(access토큰 받음)
//        // => "userRequest"가 감고 있는 정보
//        //회원 프로필을 받아야하는데 여기서 사용되는것이 "loadUser" 함수이다 -> 구글 로 부터 회원 프로필을 받을수 있다.
//        /**
//         * OAuth 로그인 회원 가입
//         */
//
////        println("getClientRegistration: " + userRequest.clientRegistration)
////        println("getAccessToken: " + userRequest.accessToken.tokenValue)
////        println("getAttributes: " + super.loadUser(userRequest).attributes)
//
//
//        return oAuthToUser(userRequest)
//
//    }
//
//    private fun oAuthToUser(userRequest: OAuth2UserRequest): Mono<OAuth2User> {
//        val delegate = DefaultReactiveOAuth2UserService()
//        return delegate.loadUser(userRequest)
//            .doOnNext {
//                logger.info { "PRINCIPAL 지나감" }
//            }
//            .map { oAuth2User ->
//                var oAuth2UserInfo: OAuth2UserInfo? = null
//                when (userRequest.clientRegistration.registrationId) {
//                    "google" -> {
//                        oAuth2UserInfo = GoogleUserInfo(oAuth2User.attributes)
//                    }
//
//                    "naver" -> {
//                        oAuth2UserInfo = NaverUserInfo(oAuth2User.attributes["response"] as Map<String, Any>)
//                    }
//
//                    "kakao" -> {
//                        oAuth2UserInfo = KakaoUserInfo(
//                            oAuth2User.attributes["kakao_account"] as Map<String, Any>,
//                            oAuth2User.attributes["id"].toString()
//                        )
//                    }
//
//                    else -> {
//                        println("지원하지 않은 로그인 서비스 입니다.")
//                    }
//                }
//                Pair(oAuth2UserInfo!!, oAuth2User.attributes)
//            }
//            .flatMap { p ->
//                Mono.zip(userInfo2User(p.first), Mono.just(p.second))
//            }
//            .map { p ->
//                PrincipalDetails(p.t1, p.t2) as OAuth2User
//            }
//    }
//
//    private fun userInfo2User(oAuth2UserInfo : OAuth2UserInfo): Mono<UserR2dbc> {
//        val provider: OAuthProvider = oAuth2UserInfo.provider
//        val providerId: String = oAuth2UserInfo.providerId
//        val password = providerId //중요하지 않음 그냥 패스워드 암호화 하
//        val email: String = oAuth2UserInfo.email
//        val username = provider.name + "_" + email
//        val role: Role = Role.USER
//
//        return userService.findUserByName(username).switchIfEmpty{
//                    val user = UserR2dbc(
//                        name = username,
//                        password = password,
//                        email = email,
//                        role = role,
//                        provider = provider
//                    )
//                    userService.save(user)
//            }
//        //처음 서비스를 이용한 회원일 경우
//    }
//}