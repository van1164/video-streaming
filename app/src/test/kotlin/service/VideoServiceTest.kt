package service

import com.van1164.app.AppApplication
import com.van1164.video.video.VideoService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [AppApplication::class])
class VideoServiceTest @Autowired constructor(
    private val videoService: VideoService
) {

    @BeforeEach
    fun beforeEach(){
        videoService
    }

    @Test
    fun getMainPageTest(){

    }
}