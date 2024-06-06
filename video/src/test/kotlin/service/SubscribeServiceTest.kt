package service

import com.van1164.common.redis.RedisRepository
import com.van1164.common.test.Transaction
import com.van1164.user.subscribe.service.SubscribeService
import com.van1164.util.SUBSCRIBE_PREFIX
import com.van1164.video.VideoApplication
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.Rollback
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


@SpringBootTest(classes = [VideoApplication::class])
class SubscribeServiceTest @Autowired constructor(
    val subscribeService: SubscribeService,
    val transaction: Transaction,
    val redisRepository: RedisRepository,
) {

    val testEmail = "GOOGLE_van1154van@gmail.com"

    @BeforeEach
    fun beforeEach(){
        redisRepository.remove(SUBSCRIBE_PREFIX + testEmail).block()
    }

    @AfterEach
    fun afterEach(){
        redisRepository.remove(SUBSCRIBE_PREFIX + testEmail).block()
    }

    @Test
    @DisplayName("구독 성공 테스트")
    @Rollback(value = true)
    fun successTest(){
        subscribeService.subscribe(testEmail,testEmail)
            .`as`(transaction::withRollback)
            .`as`(StepVerifier::create)
            .assertNext{response->
                assertNotNull(response)
                assertEquals(response.statusCode, HttpStatus.OK)
                assertNotNull(response.body)
                assertEquals(response.body,"success")
            }
            .verifyComplete()

        redisRepository.load(SUBSCRIBE_PREFIX+testEmail)
            .defaultIfEmpty("X")
            .`as`(StepVerifier::create)
            .assertNext{
                assertEquals(it,"1")
            }
            .verifyComplete()

    }

    @Test
    @DisplayName("없는 아이디로 인한 구독 실패 테스트")
    @Rollback(value = true)
    fun noUserIdFailTest(){
        subscribeService.subscribe("test@gmail.com",testEmail)
            .`as`(transaction::withRollback)
            .`as`(StepVerifier::create)
            .assertNext{response->
                assertNotNull(response)
                assertEquals(response.statusCode,HttpStatus.BAD_REQUEST)
                assertNotNull(response.body)
                assertEquals(response.body ,"존재하지 않는 User")
            }
            .verifyComplete()

        redisRepository.load(SUBSCRIBE_PREFIX+testEmail)
            .defaultIfEmpty("X")
            .`as`(StepVerifier::create)
            .assertNext{
                assertEquals(it,"X")
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("이미 구독한 사용자 실패 테스트")
    @Rollback(value = true)
    fun duplicatedSubscribeFailTest(){
        subscribeService.subscribe(testEmail,testEmail)
            .flatMap {
                subscribeService.subscribe(testEmail,testEmail)
            }
            .`as`(transaction::withRollback)
            .`as`(StepVerifier::create)
            .assertNext{response->
                assertNotNull(response)
                assertEquals(response.statusCode,HttpStatus.BAD_REQUEST)
                assertNotNull(response.body)
                assertEquals(response.body ,"이미 구독 중")
            }
            .verifyComplete()

        redisRepository.load(SUBSCRIBE_PREFIX+testEmail)
            .defaultIfEmpty("X")
            .`as`(StepVerifier::create)
            .assertNext{
                assertEquals(it,"1")
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("구독 취소 성공 테스트")
    @Rollback(value = true)
    fun subscribeCancel(){
        subscribeService.subscribe(testEmail,testEmail)
            .doOnNext{ response ->
                assertNotNull(response)
                assertEquals(response.statusCode, HttpStatus.OK)
                assertNotNull(response.body)
                assertEquals(response.body, "success")
            }
            .flatMap {
                subscribeService.subscribeCancel(testEmail,testEmail)
            }
            .`as`(transaction::withRollback)
            .`as`(StepVerifier::create)
            .assertNext{cancel->
                assertNotNull(cancel)
                assertEquals(cancel.statusCode,HttpStatus.OK)
                assertNotNull(cancel.body)
                assertEquals(cancel.body ,"success")
            }
            .verifyComplete()


        redisRepository.load(SUBSCRIBE_PREFIX+testEmail)
            .`as`(StepVerifier::create)
            .assertNext{
                assertEquals(it,"0")
            }
            .verifyComplete()

    }

}