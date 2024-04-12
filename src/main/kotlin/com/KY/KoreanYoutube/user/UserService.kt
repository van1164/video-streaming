package com.KY.KoreanYoutube.user

import com.KY.KoreanYoutube.domain.User
import jakarta.transaction.Transactional

import org.springframework.stereotype.Service


@Service
class UserService(
    val userRepository: UserRepository
) {

}