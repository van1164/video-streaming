package com.KY.KoreanYoutube.security

import java.util.UUID

interface OAuth2UserInfo {
    val providerId: String
    val provider: OAuthProvider
    val email: String
    val name: String
}

enum class OAuthProvider{
    NAVER,
    KAKAO,
    GOOGLE
}


class GoogleUserInfo(private val attributes: Map<String, Any>) : OAuth2UserInfo {
    override val providerId: String
        get() = attributes["sub"].toString()

    override val provider: OAuthProvider
        get() = OAuthProvider.GOOGLE

    override val email: String
        get() = attributes["email"].toString()

    override val name: String
        get() = attributes["name"].toString()
}

class NaverUserInfo(private val attributes: Map<String, Any>) : OAuth2UserInfo {
    override val providerId: String
        get() = attributes["id"].toString()

    override val provider: OAuthProvider
        get() = OAuthProvider.NAVER

    override val email: String
        get() = attributes["email"].toString()

    override val name: String
        get() = attributes["name"].toString()
}

class KakaoUserInfo(private val kakaoAccount: Map<String, Any>, override val providerId: String) : OAuth2UserInfo {
    override val provider: OAuthProvider
        get() = OAuthProvider.KAKAO

    override val email: String
        get() = kakaoAccount["email"].toString()

    override val name: String
        get() = UUID.randomUUID().toString()
}