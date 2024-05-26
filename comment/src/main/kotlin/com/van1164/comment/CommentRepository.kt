package com.van1164.comment

import com.van1164.common.domain.Comment
import com.van1164.common.dto.CommentDTO
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.relational.core.sql.LockMode
import org.springframework.data.relational.repository.Lock
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface CommentRepository : R2dbcRepository<Comment,Long> {
//    @Lock(LockMode.PESSIMISTIC_WRITE)


}