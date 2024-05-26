package com.van1164.comment

import com.van1164.common.domain.Comment
import com.van1164.security.PrincipalDetails
import com.van1164.user.UserService
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/comment")
class CommentController(
    val commentService: CommentService,
    val userService: UserService
) {
    val logger= KotlinLogging.logger {  }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{videoId}")
    fun createComment(
        @AuthenticationPrincipal principalDetails: Mono<PrincipalDetails>,
        @PathVariable(name = "videoId") videoId: String,
        @RequestBody comment: String
    ): Mono<ResponseEntity<Comment>> {
        println(SecurityContextHolder.getContext().toString())
        return principalDetails
            .doOnNext {
                logger.info {comment }
            }
            .flatMap {user->
                commentService.createComment(user.name,videoId,comment)
            }
    }

    @GetMapping("/like/{commentId}")
    fun likeComment(
        @AuthenticationPrincipal principalDetails: Mono<PrincipalDetails>,
        @PathVariable(name = "commentId") commentId: Long,
    ): Mono<ResponseEntity<Any>> {
        return principalDetails
            .flatMap {user->
                commentService.likeComment(user.name,commentId)
            }
    }

    @GetMapping("/{commentId}")
    fun getComment(
//        @AuthenticationPrincipal principalDetails: Mono<PrincipalDetails>,
        @PathVariable(name = "commentId") commentId: Long,
    ): Mono<ResponseEntity<Any>> {
        return commentService.getComment(commentId)
    }
}