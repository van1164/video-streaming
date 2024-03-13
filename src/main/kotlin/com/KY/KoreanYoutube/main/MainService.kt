package com.KY.KoreanYoutube.main

import com.KY.KoreanYoutube.video.VideoRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class MainService(
    val mainRepository: MainRepository,
    val videoRepository: VideoRepository
) {
    fun getMainPage(): Any? {
        val data = hashMapOf<Any,Any>()
        data["videoList"] = videoRepository.findAll(Sort.by("createDate").descending())
        return data
    }
}