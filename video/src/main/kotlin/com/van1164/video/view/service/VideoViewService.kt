package com.van1164.video.view.service

import com.van1164.common.domain.VideoView
import com.van1164.common.redis.RedisR2dbcRepository
import com.van1164.util.VIDEO_VIEW_PREFIX
import com.van1164.video.view.repository.VideoViewRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class VideoViewService(
    private val videoViewRepository: VideoViewRepository,
    private val redisR2dbcRepository: RedisR2dbcRepository
) {

    fun videoView(userId:String, videoId:Long): Mono<ResponseEntity<Long>> {
        return VideoView(videoId,userId).toMono()
            .flatMap {
                videoViewRepository.save(it)
            }
            .flatMap {
                redisR2dbcRepository.increment(VIDEO_VIEW_PREFIX + it.videoId)
            }
            .map {
                ResponseEntity.ok(it)
            }
            .onErrorReturn(ResponseEntity.internalServerError().build())

    }
}