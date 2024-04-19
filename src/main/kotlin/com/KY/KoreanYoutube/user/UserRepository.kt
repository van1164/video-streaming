package com.KY.KoreanYoutube.user

import com.KY.KoreanYoutube.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository:JpaRepository<User,String> {
    fun findUserByUserId(userId:String):User?
}