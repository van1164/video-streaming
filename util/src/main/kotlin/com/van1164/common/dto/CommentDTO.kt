package com.van1164.common.dto

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import java.time.LocalDateTime

data class CommentDTO(
    val message : String,

    val userName : String,

    val videoId: Long,

    val createdDate : LocalDateTime,

    val id : Long,
    val good : Int,
    val bad : Int
)
