package com.van1164.main_module


import com.van1164.common.aspect.LoggingStopWatch
import com.van1164.common.response.MainResponse
import com.van1164.main_module.live.LiveReadService
import com.van1164.main_module.video.VideoReadService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class MainService(
    val videoService: VideoReadService,
    val streamService: LiveReadService
) {
    @LoggingStopWatch
    @Transactional(readOnly = true)
    fun getMainPage(): Mono<HashMap<String,Any>> {
        val mainData = hashMapOf<String,Any>()
        return videoService.findAllSortByDescending().collectList()
            .doOnNext {
                mainData["videoList"] = it
            }
            .map {
                streamService.findAllOnAir().collectList()
            }
            .doOnNext {
                mainData["streamList"] = it
            }
            .thenReturn(mainData)
    }

    @LoggingStopWatch
    @Transactional(readOnly = true)
    fun getMainResponse(): Mono<MainResponse> {
        val mainData = MainResponse()
        return videoService.findAllSortByDescending().collectList()
            .doOnNext {
                mainData.videoList = it
            }
            .flatMap {
                streamService.findAllOnAir().collectList()
            }
            .doOnNext {
                mainData.liveList = it
            }
            .thenReturn(mainData)
    }
}