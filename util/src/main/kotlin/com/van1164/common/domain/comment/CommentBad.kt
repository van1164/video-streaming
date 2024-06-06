package com.van1164.common.domain.comment

import org.springframework.data.annotation.Id

data class CommentBad(
    val commentId : Long,

    val userId : String,

    @Id
    val id : Long? = null
)
