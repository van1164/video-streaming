package com.van1164.common.domain

import org.springframework.data.annotation.Id


data class VideoView(

    val videoId : Long,

    val userId : String,

    @Id
    val id : Long? = null
)
