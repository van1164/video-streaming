package com.KY.KoreanYoutube.domain

import com.KY.KoreanYoutube.security.OAuthProvider
import com.KY.KoreanYoutube.security.Role
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import java.time.LocalDateTime

@Table(name = "user_r2dbc")
data class UserR2dbc(

    val name: String,
    val email : String,
    val provider : OAuthProvider,
    val password : String = "oAuthPassword",
    val role : Role = Role.USER,

    @CreatedDate
    val createDate : LocalDateTime = LocalDateTime.now(),

    var onAir: Boolean = false,

    @Id
    val id : Long? =null
)
