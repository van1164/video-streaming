package com.van1164.main

import com.van1164.common.util.Utils.logger
import com.van1164.security.JwtTokenProvider
import com.van1164.user.UserService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.MOVED_PERMANENTLY
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.onErrorReturn
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
        logger.info { "TESTCCCCCCCXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" }
        logger.info{token}
        return  mainService.getMainPage()
                .doOnNext {mainData->
                    model.addAllAttributes(mainData)
                    logger.info{mainData}
                }
                .doOnNext {
                    checkNotNull(token)
                    check(token.isNotEmpty())
                }
                .flatMap {
                    jwtTokenProvider.getAuthentication(token!!)
                }
                .flatMap {name->
                    userService.findByUserId(name.name)
                }.doOnNext {user->
                    checkNotNull(user)
                }
                .doOnNext {user->
                    model.addAttribute("user",user.name)
                    model.addAttribute("jwt",token)
                    model.addAttribute("isLogined", "true")
                }
                .doOnError {
                    logger.info { "NULL" }
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