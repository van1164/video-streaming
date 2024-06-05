package com.van1164.common.domain.comment

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("comment_like")
data class CommentLike(

    val commentId : Long,

    val userId : String,

    @Id
    val id : Long? = null
)
