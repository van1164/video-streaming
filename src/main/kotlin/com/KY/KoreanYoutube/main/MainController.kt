package com.KY.KoreanYoutube.main

import com.KY.KoreanYoutube.config.log
import com.KY.KoreanYoutube.security.JwtTokenProvider
import com.KY.KoreanYoutube.security.logger
import com.KY.KoreanYoutube.user.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus.MOVED_PERMANENTLY
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.net.URI

@Controller
@RequestMapping("/")
class MainController(
    val mainService: MainService,
    val jwtTokenProvider: JwtTokenProvider,
    val userService: UserService
) {

    @GetMapping("/")
    fun mainPage(model : Model,@RequestParam(required = false) token : String?): String {
        val mainData = mainService.getMainPage()
        model.addAllAttributes(mainData)
        if(!token.isNullOrEmpty()){
            logger.info { "++++++++++++++++++++++++++++++로그인됨" }
            val name = jwtTokenProvider.getAuthentication(token).name
            val user = userService.findByUserId(name)
            if(user !=null){
                model.addAttribute("user",user.name)
                model.addAttribute("jwt",token)
                model.addAttribute("isLogined", "true")
            }
            else{
                model.addAttribute("user","null")
                model.addAttribute("isLogined", "false")
            }
        }
        else{
            model.addAttribute("user","null")
            model.addAttribute("isLogined", "false")
        }
        return "main"

    }

    @GetMapping("/access/google")
    fun googleLogin(@RequestParam code : String) : ResponseEntity<Any>{
        val headers = HttpHeaders()
        headers.location = URI.create("/")
        logger.info { "코드 ====== "+code }
        headers.set("Auth",code)
        return ResponseEntity(headers, MOVED_PERMANENTLY)

    }

    @GetMapping("/loginPage")
    fun loginPage() : String{

        return "loginPage"

    }

}