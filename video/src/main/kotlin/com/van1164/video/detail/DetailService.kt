package com.van1164.video.detail

import com.van1164.comment.CommentReadRepository
import com.van1164.common.response.DetailResponse
import com.van1164.main_module.video.VideoReadRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono


@Service
class DetailService(
    @Value("\${aws.s3.bucketUrl}")
    val bucketUrl : String,
    val videoRepository: VideoReadRepository,
    val commentRepository: CommentReadRepository
) {
    fun loadDetail(detailId : String): Mono<ResponseEntity<DetailResponse>> {
        println(detailId)
        return videoRepository.findByUrl(detailId)
            .flatMap {
                println(it.id)
                Mono.zip(
                    commentRepository.findAllByVideoId(it.id!!).collectList(),
                    it.toMono()
                )
            }
            .map {
                println(it.t1)
                ResponseEntity.ok(
                    DetailResponse(
                        videoR2dbc = it.t2,
                        comments = it.t1,
                        "$bucketUrl$detailId/$detailId.m3u8"
                    )
                )
            }
            .switchIfEmpty(ResponseEntity.badRequest().build<DetailResponse>().toMono())
            .onErrorReturn(ResponseEntity.internalServerError().build())
    }

}