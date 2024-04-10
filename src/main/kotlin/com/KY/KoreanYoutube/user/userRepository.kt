package com.KY.KoreanYoutube.user

import com.KY.KoreanYoutube.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface userRepository:JpaRepository<User,Long> {
}