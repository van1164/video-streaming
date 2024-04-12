package com.KY.KoreanYoutube.user

import com.KY.KoreanYoutube.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository:JpaRepository<User,String> {

    fun findUserByName(name:String):User?
}