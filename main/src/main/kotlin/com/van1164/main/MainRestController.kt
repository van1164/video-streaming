package com.van1164.main

import com.van1164.common.response.MainResponse
import com.van1164.common.util.Utils.logger
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.net.URI

@RestController
@RequestMapping("/api/v1/main")
class MainRestController(
    val mainService: MainService,
) {

    @GetMapping("")
    fun mainPage(): Mono<ResponseEntity<MainResponse>> {
        logger.info { "TESTCCCCCCCXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" }
        return  mainService.getMainResponse()
            .map{ResponseEntity.ok(it)}
            .onErrorReturn(ResponseEntity.badRequest().body(MainResponse()))
    }

    @GetMapping("/access/google")
    fun googleLogin(@RequestParam code : String) : ResponseEntity<Any> {
        val headers = HttpHeaders()
        headers.location = URI.create("/")
        logger.info { "코드 ====== "+code }
        headers.set("Auth",code)
        return ResponseEntity(headers, HttpStatus.MOVED_PERMANENTLY)

    }

}