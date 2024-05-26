package com.van1164.comment.like

import com.van1164.common.domain.CommentLike
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CommentLikeRepository : R2dbcRepository<CommentLike,Long> {
    fun countAllByCommentId(commentId: Long) : Mono<Int>
    fun findAllByCommentId(commentId : Long) : Flux<CommentLike>
}