package com.van1164.main

import com.van1164.common.util.Utils.logger
import com.van1164.security.JwtTokenProvider
import com.van1164.common.security.PrincipalDetails
import com.van1164.user.UserService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.MOVED_PERMANENTLY
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import reactor.core.publisher.Mono
import java.net.URI

@Controller
@RequestMapping("")
class MainController(
    val mainService: MainService,
    val jwtTokenProvider: JwtTokenProvider,
    val userService: UserService
) {

    @GetMapping("/")
    fun mainPage(model : Model,@RequestParam(required = false) token : String?): Mono<String> {
        return  mainService.getMainPage()
                .doOnNext {mainData->
                    model.addAllAttributes(mainData)
                }
                .map {
                    checkNotNull(token)
                    check(token.isNotEmpty())
                    token
                }
                .flatMap {
                    jwtTokenProvider.getAuthentication(it)
                }
                .map {name->
                    checkNotNull((name.principal as PrincipalDetails).name)
                }
                .flatMap {userId->
                    userService.findByUserId(userId)
                }.doOnNext {user->
                    println(user.name)
                    checkNotNull(user)
                }
                .doOnNext {user->
                    model.addAttribute("user",user.name)
                    model.addAttribute("jwt",token)
                    model.addAttribute("isLogined", "true")
                }
                .doOnError {
                    logger.error { "NULL" }
                    model.addAttribute("user","null")
                    model.addAttribute("isLogined", "false")
                }
                .thenReturn("main")
                .onErrorReturn("main")
    }

    @GetMapping("/access/google")
    fun googleLogin(@RequestParam code : String) : ResponseEntity<Any>{
        val headers = HttpHeaders()
        headers.location = URI.create("/")
        logger.info { "코드 ====== "+code }
        headers.set("Auth",code)
        return ResponseEntity(headers, MOVED_PERMANENTLY)

    }
//    @GetMapping("/loginPage")
//    fun loginPage() : String{
//
//        return "loginPage"
//
//    }

}