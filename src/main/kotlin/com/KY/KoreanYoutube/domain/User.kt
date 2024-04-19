package com.KY.KoreanYoutube.domain

import com.KY.KoreanYoutube.security.OAuthProvider
import com.KY.KoreanYoutube.security.Role
import jakarta.persistence.*
import lombok.Builder
import org.springframework.security.core.userdetails.User
import java.util.*


@Entity
@Table(name = "user")
data class User(

    @Id
    @Column(name = "user_id")
    val userId: String,

    @Column(name = "name")
    val name: String,

    @Column(name ="email")
    val email : String,

    @Column(name = "provider", nullable = false)
    val provider : OAuthProvider,

    @Column(name ="password")
    val password : String = "oAuthPassword",

    @Column(name ="role")
    val role : Role = Role.USER,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    val createDate : Date = Date.from(Date().toInstant()),

    @Column(name = "on_air")
    var onAir: Boolean = false,
)
