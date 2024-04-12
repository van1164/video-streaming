package com.KY.KoreanYoutube.user

import com.KY.KoreanYoutube.domain.User
import com.KY.KoreanYoutube.security.OAuthProvider
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.Date

@Controller
@RequestMapping("/api/v1/user")
class UserController(
    val userRepository: UserRepository
) {

    @GetMapping("/test_create")
    fun testCreate(){
        userRepository.save(User("test", "test@naver.com",OAuthProvider.GOOGLE))
    }
}