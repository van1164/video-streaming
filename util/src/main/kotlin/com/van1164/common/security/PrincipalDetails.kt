package com.van1164.common.security

import com.van1164.common.domain.UserR2dbc
import com.van1164.common.dto.OAuthProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User


class PrincipalDetails : UserDetails, OAuth2User {
    private var user  : UserR2dbc
    private lateinit var attributes :MutableMap<String,Any>
    public constructor(user: UserR2dbc) {
        this.user = user
    }

    constructor(user: UserR2dbc, attributes: Map<String, Any>){
        this.user = user
        this.attributes = attributes as MutableMap<String, Any>
    }

    override fun getName(): String {
        return user.userId
    }

    override fun getAttributes(): MutableMap<String, Any> {
        return attributes
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        val collection: ArrayList<GrantedAuthority> = ArrayList()
        collection.add(GrantedAuthority { user.role.toString() })
        return collection
    }
    override fun getPassword(): String {
        return user.password
    }

    override fun getUsername(): String {
        return user.name
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    fun getProvider(): OAuthProvider {
        return user.provider
    }
    fun getEmail():String{
        return user.email
    }
}