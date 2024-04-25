package com.KY.KoreanYoutube.main

import com.KY.KoreanYoutube.domain.VideoR2dbc
import com.KY.KoreanYoutube.main
import com.KY.KoreanYoutube.stream.StreamService
import com.KY.KoreanYoutube.video.VideoR2DBCRepository
import com.KY.KoreanYoutube.video.VideoRepository
import com.KY.KoreanYoutube.video.VideoService
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class MainService(
    val mainRepository: MainRepository,
    val videoService: VideoService,
    val streamService: StreamService
) {
    fun getMainPage(): Mono<HashMap<String,Any>> {
        val mainData = hashMapOf<String,Any>()
        return videoService.findAll(Sort.by("createDate").descending()).collectList()
            .doOnNext {
                mainData["videoList"] = it
            }
            .map {
                streamService.findAllOnAir()
            }
            .doOnNext {
                mainData["streamList"] = it
            }
            .thenReturn(mainData)
    }
}