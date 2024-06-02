package com.van1164.common.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("video_view")
data class VideoView(

    val videoId : Long,

    val userId : String,

    @Id
    val id : Long? = null
)
