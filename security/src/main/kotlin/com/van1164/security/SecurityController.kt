package com.van1164.security

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class SecurityController {

    @GetMapping("/loginPage")
    fun loginPage() : String{

        return "loginPage"

    }
}