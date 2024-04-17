package com.KY.KoreanYoutube.video

import com.KY.KoreanYoutube.domain.Video
import com.KY.KoreanYoutube.domain.VideoR2dbc
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface VideoR2DBCRepository : R2dbcRepository<VideoR2dbc,String> {
    fun findFirstByUrl(url: String):Mono<VideoR2dbc>

}