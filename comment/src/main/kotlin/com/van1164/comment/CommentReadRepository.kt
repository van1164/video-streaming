package com.van1164.comment

import com.van1164.common.dto.CommentDTO
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface CommentReadRepository : R2dbcRepository<CommentDTO,Long>{

    @Query("select\n" +
            " c.*, COUNT(b.comment_id) as bad " +
            "from comment_bad b\n" +
            "    right outer join (select c.* ,Count(l.comment_id) as good from comment c left outer join comment_like l on c.id = l.comment_id where c.video_id =:videoId group by c.id) c\n" +
            "        ON b.comment_id = c.id where c.video_id =:videoId  \n" +
            "group by c.id")
    fun findAllByVideoId(videoId:Long) : Flux<CommentDTO>


    @Query("select\n" +
            " c.*, COUNT(b.comment_id) as bad" +
            "from comment_bad b\n" +
            "    right outer join (select c.* ,Count(l.comment_id) as good from comment c left outer join comment_like l on c.id = l.comment_id where c.id =:id group by c.id) c\n" +
            "        ON b.comment_id = c.id where c.id =:id  \n" +
            "group by c.id")
    override fun findById(id : Long): Mono<CommentDTO>
}