package com.KY.KoreanYoutube.user

import com.KY.KoreanYoutube.domain.User

import org.springframework.stereotype.Service


@Service
class UserService(
    val userRepository: UserRepository
) {
    fun findByUserId(name : String): User? {
        return userRepository.findUserByUserId(name)
    }

    fun save(user: User): User {
        return userRepository.save(user)
    }

}