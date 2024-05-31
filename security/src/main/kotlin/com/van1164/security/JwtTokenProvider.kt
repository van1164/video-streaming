package com.van1164.security

import com.nimbusds.oauth2.sdk.Role
import com.van1164.common.redis.RedisService
import com.van1164.user.UserService
import com.van1164.util.JWT_PREFIX
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
import reactor.core.publisher.Mono
import java.util.*
import kotlin.collections.LinkedHashMap

@Component
@RequiredArgsConstructor
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    private var secretKey: String,
    private val redisService: RedisService,
    private val userService: UserService
) {

    val EXPIRATION_MILLISECONDS: Long = 10000 * 60 * 30
    private val key by lazy { Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)) }


    fun createToken(email: String): TokenInfo {
        val claims = Jwts.claims().apply {
            put("roles", Role.entries)
        }
        val now = Date()
        val accessExpiration = Date(now.time + EXPIRATION_MILLISECONDS)

        val accessToken = Jwts
            .builder()
            .claim("auth", claims)
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(accessExpiration)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()

        return TokenInfo("Bearer", accessToken)
    }

    fun getAuthentication(token: String): Mono<UsernamePasswordAuthenticationToken> {
        val claims = getClaims(token)
        println("TTTTTTTTTTTTTTTTT")
        println(claims.subject)
        println(claims.entries)
        println("XXXXXXXXXXXXXXXXXXX")
        val auth = claims["auth"] ?: throw RuntimeException("잘못된 토큰입니다.")
        val userId = claims.subject
        val authorities: Collection<GrantedAuthority> = (auth.toString())
            .split(",")
            .map { SimpleGrantedAuthority(it) }

       return userService.findByUserId(userId)
           .map{
               PrincipalDetails(it)
           }
           .map{principal->
               UsernamePasswordAuthenticationToken(principal,"",authorities)
           }
    }

    fun validateToken(token: String): Boolean {
        try {
            getClaims(token)
            redisService.loadByJwt(token)?: return false
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