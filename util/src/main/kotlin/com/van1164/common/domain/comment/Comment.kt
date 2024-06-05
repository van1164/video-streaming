package com.van1164.common.domain.comment


import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import java.time.LocalDateTime


data class Comment(

    val message : String,

    val userName : String,

    val videoId: Long,

    var good : Int = 0,
    var bad : Int = 0,


    @CreatedDate
    val createdDate : LocalDateTime = LocalDateTime.now(),

    @Id
    val id : Long? = null
)
