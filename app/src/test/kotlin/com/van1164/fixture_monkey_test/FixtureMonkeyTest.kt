package com.van1164.fixture_monkey_test

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMe
import com.van1164.common.domain.VideoR2dbc
import com.van1164.common.domain.comment.Comment
import org.junit.jupiter.api.Test

class FixtureMonkeyTest {

    var fixtureMonkey: FixtureMonkey = FixtureMonkey.builder()
        .plugin(KotlinPlugin())
        .build()

    @Test
    fun test(){
        val video = fixtureMonkey.giveMeOne(VideoR2dbc::class.java)
        println(video)
    }
}