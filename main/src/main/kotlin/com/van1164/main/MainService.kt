package com.van1164.main


import com.van1164.main.live.LiveReadService
import com.van1164.main.video.VideoReadService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MainService(
    val videoService: VideoReadService,
    val streamService: LiveReadService
) {
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
}