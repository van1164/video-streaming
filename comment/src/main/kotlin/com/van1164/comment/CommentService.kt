package com.van1164.comment

import com.van1164.comment.like.CommentLikeRepository
import com.van1164.common.domain.comment.Comment
import com.van1164.common.domain.comment.CommentLike
import com.van1164.common.util.Utils.logger
import com.van1164.main.video.VideoReadRepository
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
@EnableR2dbcRepositories(basePackageClasses = [CommentRepository::class,CommentReadRepository::class])
class CommentService(
    val videoReadRepository: VideoReadRepository,
    val commentRepository: CommentRepository,
    val commentLikeRepository: CommentLikeRepository
) {

    fun createComment(userName: String, videoUrl: String, comment: String): Mono<ResponseEntity<Comment>> {
        logger.info { "createCommnet" }
        return videoReadRepository.findByUrl(videoUrl)
            .flatMap {video->
                val videoId = checkNotNull(video.id)
                commentRepository.save(
                    Comment(comment, userName,videoId)
                )
                .map {
                    ResponseEntity.ok(it)
                }
            }
            .log()
            .onErrorReturn (
                ResponseEntity.badRequest().build()
            )
    }

    @Transactional
    fun likeComment(userName: String, commentId: Long): Mono<ResponseEntity<Any>> {
        return CommentLike(commentId,userName)
            .toMono()
            .flatMap {
                commentLikeRepository.save(it)
            }
            .thenReturn<ResponseEntity<Any>?>(ResponseEntity.ok().build())
            .onErrorReturn(ResponseEntity.internalServerError().build())

//        return commentRepository.findById(commentId)
//            .doOnNext {
//                it.good +=1
//            }
//            .flatMap {
//                logger.info { it }
//                commentRepository.save(it)
//            }
//            .thenReturn<ResponseEntity<Any>?>(ResponseEntity.ok().build())
//            .onErrorReturn(ResponseEntity.internalServerError().build())
    }

    fun getComment(commentId :Long): Mono<ResponseEntity<Any>> {
        return commentRepository
            .findById(commentId)
            .map{
                ResponseEntity.ok(it)
            }
    }

//    fun getCommentAndLike(commentId :Long): Mono<ResponseEntity<Any>> {
//        return commentRepository
//            .findById(commentId)
//            .zipWith(commentLikeRepository.countAllByCommentId(commentId))
//            .map{
//                it.t1.good = it.t2
//                ResponseEntity.ok(it.t1)
//            }
//    }
}