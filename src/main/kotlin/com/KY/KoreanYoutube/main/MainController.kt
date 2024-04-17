package com.KY.KoreanYoutube.main

import com.KY.KoreanYoutube.config.log
import com.KY.KoreanYoutube.security.logger
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
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
    val mainService: MainService
) {

    @GetMapping("/")
    fun mainPage(request:HttpServletRequest,model : Model): String {
        val item = mainService.getMainPage()
        logger.info{item}
        model.addAttribute("videoList", item)
        val jwt = request.getHeader("Auth")
        model.addAttribute("auth",jwt)
        log.info { jwt }
        return "main"

    }

    @GetMapping("/access/google")
    fun googleLogin(@RequestParam code : String) : ResponseEntity<Any>{
        val headers = HttpHeaders()
        headers.location = URI.create("/")
        headers.set("Auth",code)
        return ResponseEntity(headers, MOVED_PERMANENTLY)

    }

    @GetMapping("/loginPage")
    fun loginPage() : String{

        return "loginPage"

    }

}