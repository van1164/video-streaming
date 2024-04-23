package com.KY.KoreanYoutube.stream

import com.KY.KoreanYoutube.domain.LiveStream
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StreamRepository :JpaRepository<LiveStream,Long>{
    fun findByOnAirIsTrueOrderByCreateDateDesc():List<LiveStream>

    fun findFirstByStreamKey(streamKey : String):LiveStream?
}