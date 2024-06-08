package repository

import com.van1164.app.AppApplication
import com.van1164.video.video.VideoRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier

@SpringBootTest(classes = [AppApplication::class])
class VideoRepositoryTest @Autowired constructor(
    private val videoRepository: VideoRepository
) {

    @Test
    @DisplayName("동영상 cursor 방식 테스트")
    fun findAllByOrderByCreatedDateDescWithCursorTest(){
        videoRepository.findAllByOrderByCreatedDateDescWithCursor(43,10)
            .`as`(StepVerifier::create)
            .expectNextMatches {
                it.id == 42L
            }
            .expectNextMatches {
                it.id == 41L
            }
            .expectNextMatches {
                it.id == 40L
            }
            .expectNextMatches {
                it.id == 39L
            }
            .expectNextMatches {
                it.id == 38L
            }
            .expectNextMatches {
                it.id == 37L
            }
            .expectNextMatches {
                it.id == 36L
            }
            .expectNextMatches {
                it.id == 35L
            }
            .expectNextMatches {
                it.id == 34L
            }
            .expectNextMatches {
                it.id == 33L
            }
            .verifyComplete()

    }

}