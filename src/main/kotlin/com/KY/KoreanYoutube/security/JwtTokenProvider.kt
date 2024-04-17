package com.KY.KoreanYoutube.security

import com.KY.KoreanYoutube.redis.RedisRepository
import com.nimbusds.oauth2.sdk.Role
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.LinkedHashMap

@Component
@RequiredArgsConstructor
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    var secretKey: String,
    val redisRepository: RedisRepository
) {

    val EXPIRATION_MILLISECONDS: Long = 10000 * 60 * 30
    private val key by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)) }


    fun createToken(email: String): TokenInfo {
        val claims = Jwts.claims().apply {
            subject = email
            put("roles", Role.entries)
        }
        val now = Date()
        val accessExpiration = Date(now.time + EXPIRATION_MILLISECONDS)

        val accessToken = Jwts
            .builder()
            .claim("auth", claims)
            .setIssuedAt(now)
            .setExpiration(accessExpiration)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()

        return TokenInfo("Bearer", accessToken)
    }

    fun getAuthentication(token: String): Authentication {
        val claims = getClaims(token)
        val auth = claims["auth"] ?: throw RuntimeException("잘못된 토큰입니다.")
        val email = (auth as LinkedHashMap<*, *>)["sub"] as String
        val authorities: Collection<GrantedAuthority> = (auth.toString())
            .split(",")
            .map { SimpleGrantedAuthority(it) }

        val principal: UserDetails = User(email, "", authorities)
        println(principal)
        println(email)  //redis에서 토큰 있는지도 한번 더 체크 구현해야함
        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    fun validateToken(token: String): Boolean {
        try {
            getClaims(token)
            redisRepository.loadByJwt(token)?.run{return false}
            return true
        } catch (e: Exception) {
            println(e.message)
        }
        return false
    }

    private fun getClaims(token: String): Claims {
        return Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }
}