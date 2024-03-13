package com.KY.KoreanYoutube.video

import com.KY.KoreanYoutube.domain.Video
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoRepository : JpaRepository<Video,String> {

}