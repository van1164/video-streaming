package com.van1164.comment

import com.van1164.common.domain.comment.Comment
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : R2dbcRepository<Comment,Long> {
}