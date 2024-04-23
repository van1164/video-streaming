package com.KY.KoreanYoutube

import com.KY.KoreanYoutube.domain.User
import com.KY.KoreanYoutube.dto.StreamDTO
import com.KY.KoreanYoutube.security.OAuthProvider
import com.KY.KoreanYoutube.stream.StreamService
import com.KY.KoreanYoutube.user.UserService
import io.kotest.core.spec.style.AnnotationSpec
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor


@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class LiveStreamTest @Autowired constructor(
    val streamService: StreamService,
    val userService: UserService,
) {

    @Test
    fun liveStreamTest(){
        userService.save(User("test_user","name","email", OAuthProvider.GOOGLE))
        val streamKey = streamService.saveStream(StreamDTO("testTitle","des"),"test_user").body as String
        streamService.startStream(streamKey).subscribe()
        streamService.verifyStream(streamKey)
        streamService.doneStream(streamKey)

        val stream = streamService.findByStreamKey(streamKey)
        assert (stream !=null)
        assert (!stream!!.onAir)

        val user = userService.findByUserId("test_user")
        assert (user != null)
        assert (!user!!.onAir)
    }


}