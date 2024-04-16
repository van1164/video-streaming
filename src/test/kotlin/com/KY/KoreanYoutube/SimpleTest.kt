package com.KY.KoreanYoutube

import mu.KotlinLogging
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers


val logger = KotlinLogging.logger{}
class SimpleTest {

    @Test
    fun fluxTest(){
        Flux.just(1,2,3,4)
            .subscribeOn(
                Schedulers.immediate()
            )
            .takeUntil{it ==3}
            .takeLast(1)
            .subscribe {
                logger.info{ "value : $it" }
            }
    }
}