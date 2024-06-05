package com.van1164.common.domain

import org.springframework.data.annotation.Id

data class UserSubscribe(

    val fromUserId : String,

    val toUserId : String,

    @Id
    val id : Long? = null
)
