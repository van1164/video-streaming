package com.van1164.video.video

import com.van1164.common.domain.VideoR2dbc
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface VideoRepository : R2dbcRepository<VideoR2dbc,String> {
    fun findFirstByUrl(url: String):Mono<VideoR2dbc>

    fun findAllByOrderByCreatedDateDesc(): Flux<VideoR2dbc>

    @Query("select v.* from video_r2dbc v join (select id from video_r2dbc where id< :cursorId order by id desc limit :size) s on v.id = s.id")
    fun findAllByOrderByCreatedDateDescWithCursor(cursorId :Long,size:Int) : Flux<VideoR2dbc>

    @Query("select v.* from video_r2dbc v join (select id from video_r2dbc order by id desc limit :size) s on v.id = s.id")
    fun findAllByOrderByCreatedDateDescFirst(size:Int) : Flux<VideoR2dbc>

    fun findById(id: Long): Mono<VideoR2dbc>

    fun findByUrl(url: String): Mono<VideoR2dbc>

}