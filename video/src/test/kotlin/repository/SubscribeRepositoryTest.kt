package repository

import com.van1164.user.subscribe.repository.SubscribeRepository
import com.van1164.video.VideoApplication
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(classes = [VideoApplication::class])
class SubscribeRepositoryTest(
    @Autowired
    private val subscribeRepository: SubscribeRepository
) {

    @Test
    fun findByFromUserIdAndToUserId(){
        val userSubscribe = subscribeRepository.findByFromUserIdAndToUserId("GOOGLE_van1154van@gmail.com","GOOGLE_van1154van@gmail.com").block()
        assertNotNull(userSubscribe)
        assertEquals(userSubscribe.fromUserId,"GOOGLE_van1154van@gmail.com")
    }
}