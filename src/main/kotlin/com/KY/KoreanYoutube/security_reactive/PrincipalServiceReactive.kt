package com.KY.KoreanYoutube.security_reactive

import com.KY.KoreanYoutube.domain.User
import com.KY.KoreanYoutube.domain.UserR2dbc
import com.KY.KoreanYoutube.security.logger
import com.KY.KoreanYoutube.user.UserR2DBCService
import com.KY.KoreanYoutube.user.UserService
import lombok.RequiredArgsConstructor
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
@RequiredArgsConstructor
class PrincipalServiceReactive(
    private val userService: UserR2DBCService
) : ReactiveUserDetailsService {


    //시큐리티 session => Authentication => UserDetails
    // 여기서 리턴 된 값이 Authentication 안에 들어간다.(리턴될때 들어간다.)
    // 그리고 시큐리티 session 안에 Authentication 이 들어간다.
    //함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Throws(UsernameNotFoundException::class)
    override fun findByUsername(username: String): Mono<UserDetails> {
        return userService.findByUserId(username)
            .doOnNext {
                logger.info { "여기는 PrincipalServiceReactive" }
            }
            .map{
                return@map PrincipalDetailsReactive(it)
            }
    }
}