package com.KY.KoreanYoutube.main

import com.KY.KoreanYoutube.domain.VideoR2dbc
import com.KY.KoreanYoutube.video.VideoR2DBCRepository
import com.KY.KoreanYoutube.video.VideoRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class MainService(
    val mainRepository: MainRepository,
    val videoRepository: VideoR2DBCRepository
) {
    fun getMainPage(): MutableList<VideoR2dbc>? {
        return videoRepository.findAll(Sort.by("createDate").descending()).collectList().block()
//        val empty = Flux.empty<Void>()
//        return Flux.zip(videoList,empty)
//            .map {
//                val hashMap = hashMapOf<Any,Any>()
//                hashMap["videoList"] = it.t1
//                hashMap
//            }
//            .toMono()
    }
}