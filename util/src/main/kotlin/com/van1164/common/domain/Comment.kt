package com.van1164.common.domain


import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import java.time.LocalDateTime


data class Comment(

    val message : String,

    val userName : String,

    val videoId: Long,

    @CreatedDate
    val createdDate : LocalDateTime = LocalDateTime.now(),

    @Id
    val id : Long? = null
)