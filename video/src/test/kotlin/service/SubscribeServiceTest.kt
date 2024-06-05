package service

import com.van1164.user.subscribe.service.SubscribeService
import com.van1164.video.VideoApplication
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.Rollback
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


@SpringBootTest(classes = [VideoApplication::class])
class SubscribeServiceTest @Autowired constructor(
    val subscribeService: SubscribeService
) {

    @Test
    @DisplayName("구독 성공 테스트")
    @Rollback(value = true)
    fun successTest(){
        val subscribe = subscribeService.subscribe("GOOGLE_van1154van@gmail.com","GOOGLE_van1154van@gmail.com").block()
        assert(subscribe != null)
        assert(subscribe!!.statusCode == HttpStatus.OK)
        assert(subscribe.body != null)
        assert(subscribe.body == "success")
    }

    @Test
    @DisplayName("없는 아이디로 인한 구독 실패 테스트")
    @Rollback(value = true)
    fun noUserIdFailTest(){
        val subscribe = subscribeService.subscribe("GOOGLE_van1154van@gmail.com","test@gmail.com").block()
        assertNotNull(subscribe)
        assertEquals(subscribe.statusCode,HttpStatus.BAD_REQUEST)
        assertNotNull(subscribe.body)
        assertEquals(subscribe.body ,"존재하지 않는 User")
    }

    @Test
    @DisplayName("이미 구독한 사용자 실패 테스트")
    @Rollback(value = true)
    fun duplicatedSubscribeFailTest(){
        subscribeService.subscribe("GOOGLE_van1154van@gmail.com","GOOGLE_van1154van@gmail.com").block()
        val subscribe = subscribeService.subscribe("GOOGLE_van1154van@gmail.com","GOOGLE_van1154van@gmail.com").block()
        assertNotNull(subscribe)
        assertEquals(subscribe.statusCode,HttpStatus.BAD_REQUEST)
        assertNotNull(subscribe.body)
        assertEquals(subscribe.body ,"이미 구독 중")
    }

    @Test
    @DisplayName("구독 취소 성공 테스트")
    @Rollback(value = true)
    fun subscribeCancel(){
        val subscribe = subscribeService.subscribe("GOOGLE_van1154van@gmail.com","GOOGLE_van1154van@gmail.com").block()
        assertNotNull(subscribe)
        assertEquals(subscribe.statusCode,HttpStatus.OK)
        assertNotNull(subscribe.body)
        assertEquals(subscribe.body ,"success")

        val cancel = subscribeService.subscribeCancel("GOOGLE_van1154van@gmail.com","GOOGLE_van1154van@gmail.com").block()
        assertNotNull(cancel)
        assertEquals(cancel.statusCode,HttpStatus.OK)
        assertNotNull(cancel.body)
        assertEquals(cancel.body ,"success")
    }
}