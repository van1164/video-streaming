package com.KY.KoreanYoutube.security

import com.KY.KoreanYoutube.domain.User
import com.KY.KoreanYoutube.user.UserRepository
import lombok.RequiredArgsConstructor
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
@RequiredArgsConstructor
class PrincipalService(
    private val userRepository: UserRepository
) : UserDetailsService {


    //시큐리티 session => Authentication => UserDetails
    // 여기서 리턴 된 값이 Authentication 안에 들어간다.(리턴될때 들어간다.)
    // 그리고 시큐리티 session 안에 Authentication 이 들어간다.
    //함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails? {
        val findUser: User? = userRepository.findUserByName(username)
        println("XXXXXXXXXXXXXXXXXXXXXXXXXX")
        if (findUser != null) {
            return PrincipalDetails(findUser)
        }
        return null
    }
}