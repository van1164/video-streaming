package com.van1164.common.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("video_like")
data class VideoLike(

    val videoId : Long,

    val userId : String,

    @Id
    val id : Long? = null
)
