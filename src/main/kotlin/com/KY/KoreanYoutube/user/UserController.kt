package com.KY.KoreanYoutube.user

import com.KY.KoreanYoutube.domain.User
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

@Controller
@RequestMapping("/api/v1/user")
class UserController(
    val userRepository: userRepository
) {

    @GetMapping("/test_create")
    fun testCreate(){
        userRepository.save(User("test",  Date.from(Date().toInstant())))
    }
}