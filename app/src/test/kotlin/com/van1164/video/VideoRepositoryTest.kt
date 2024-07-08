package com.van1164.video

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMe
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.van1164.app.AppApplication
import com.van1164.common.domain.VideoR2dbc
import com.van1164.video.video.VideoRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier
import kotlin.test.assertEquals

@SpringBootTest(classes = [AppApplication::class])
class VideoRepositoryTest @Autowired constructor(
    private val videoRepository: VideoRepository
) {

    var fixtureMonkey: FixtureMonkey = FixtureMonkey.builder()
        .plugin(KotlinPlugin())
        .build()

    var startId : Long? = null

    @BeforeEach
    fun setUp(){
        videoRepository.deleteAll().block()
        val insertList = fixtureMonkey.giveMeBuilder<VideoR2dbc>().setNull("id").sampleList(10)
        println(insertList)
        val videoList = videoRepository.saveAll(insertList).collectList().block()
        startId = videoList!!.last().id

    }

    @Test
    fun k6ExecutorTest(){
    }

    @Test
    @DisplayName("동영상 cursor 방식 테스트")
    fun findAllByOrderByCreatedDateDescWithCursorTest(){
        videoRepository.findAllByOrderByCreatedDateDescWithCursor(startId!!,10)
            .collectList()
            .`as`(StepVerifier::create)
            .assertNext{
                println(it)
                var index =startId!!
                for(video : VideoR2dbc in it){
                    println(index)
                    index = index.dec()
                    assertEquals(video.id,index)
                }

                assertEquals(it.size,9)
            }
            .verifyComplete()

    }

}