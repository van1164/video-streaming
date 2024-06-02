package com.van1164.video.like

import com.van1164.common.domain.VideoLike
import com.van1164.common.redis.RedisR2dbcRepository
import com.van1164.util.VIDEO_LIKE_PREFIX
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class VideoLikeService(
    private val videoLikeRepository: VideoLikeRepository,
    private val redisR2dbcRepository: RedisR2dbcRepository
) {

    fun videoLike(userName: String, videoId: Long): Mono<ResponseEntity<Long>> {
        return VideoLike(videoId,userName).toMono()
            .flatMap {
                videoLikeRepository.save(it)
            }.flatMap {
                redisR2dbcRepository.increment(VIDEO_LIKE_PREFIX+videoId)
            }
            .map {
                ResponseEntity.ok(it)
            }
            .onErrorReturn(ResponseEntity.internalServerError().build())
    }
}