package com.van1164.common.domain

import com.van1164.common.dto.OAuthProvider
import com.van1164.common.dto.Role
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
//import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

//@Table(name = "user_r2dbc")
data class UserR2dbc(

    val userId: String,
    val name : String,
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
