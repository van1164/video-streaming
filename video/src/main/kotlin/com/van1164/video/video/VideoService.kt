package com.van1164.video.video

import com.van1164.common.domain.VideoR2dbc
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class VideoService(
    private val videoRepository : VideoRepository,
    private val transactionalOperator: TransactionalOperator
) {
    fun getMainPage(cursorId : Long?, size:Int): Flux<VideoR2dbc> {
        return cursorId?.let {
            videoRepository.findAllByOrderByCreatedDateDescWithCursor(it, size)
        } ?: videoRepository.findAllByOrderByCreatedDateDescFirst(size)
    }

    fun findFirstByUrl(url:String): Mono<VideoR2dbc> {
        return videoRepository.findFirstByUrl(url)
    }

    @Transactional
    fun save(video : VideoR2dbc): Mono<VideoR2dbc> {
        return videoRepository
            .save(video)
            .`as`(transactionalOperator::transactional)
    }


}