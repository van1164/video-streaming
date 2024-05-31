package com.van1164.video.detail

import com.van1164.common.response.DetailResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/detail")
class DetailRestController (
    val detailService: DetailService,
) {

    @GetMapping("/{id}")
    fun loadDetailPage( @PathVariable(name = "id") detailId : String): Mono<ResponseEntity<DetailResponse>> {
        return detailService.loadDetail(detailId)
    }
}
