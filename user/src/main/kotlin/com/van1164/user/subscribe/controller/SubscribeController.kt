package com.van1164.user.subscribe.controller

import com.van1164.common.security.PrincipalDetails
import com.van1164.user.subscribe.service.SubscribeService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/api/v1/user/subscribe")
class SubscribeController(
    private val subscribeService: SubscribeService
) {

    @PostMapping("/{toUserId}")
    fun subscribe(
        @PathVariable("toUserId") toUserId : String,
        @AuthenticationPrincipal principal : Mono<PrincipalDetails>
    ): Mono<ResponseEntity<String>> {
        return principal.flatMap {user->
            subscribeService.subscribe(user.name,toUserId)
        }
    }

    @PostMapping("cancel/{toUserId}")
    fun subscribeCancel(
        @PathVariable("toUserId") toUserId : String,
        @AuthenticationPrincipal principal : Mono<PrincipalDetails>
    ): Mono<ResponseEntity<String>> {
        return principal.flatMap {user->
            subscribeService.subscribeCancel(user.name,toUserId)
        }
    }
}