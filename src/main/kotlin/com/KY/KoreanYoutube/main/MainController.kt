package com.KY.KoreanYoutube.main

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class MainController(
    val mainService: MainService
) {

    @GetMapping("/")
    fun mainPage(model : Model): String {
        model.addAttribute("data", mainService.getMainPage())
        return "index"

    }

}