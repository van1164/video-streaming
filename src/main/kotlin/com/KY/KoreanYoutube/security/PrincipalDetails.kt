package com.KY.KoreanYoutube.security

import com.KY.KoreanYoutube.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User


class PrincipalDetails : UserDetails, OAuth2User{
    private var user  : User
    private lateinit var attributes :MutableMap<String,Any>
    public constructor(user: User) {
        this.user = user
    }

    constructor(user: User, attributes: Map<String, Any>){
        this.user = user
        this.attributes = attributes as MutableMap<String, Any>
    }

    override fun getName(): String {
        return user.name
    }

    override fun getAttributes(): MutableMap<String, Any> {
        return attributes
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        val collection: ArrayList<GrantedAuthority> = ArrayList()
        collection.add(GrantedAuthority { java.lang.String.valueOf(user.role) })
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

    fun getProvider():OAuthProvider{
        return user.provider
    }
    fun getEmail():String{
        return user.email
    }
}