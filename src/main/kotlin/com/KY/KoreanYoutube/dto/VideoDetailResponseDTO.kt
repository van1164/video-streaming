package com.KY.KoreanYoutube.dto

import com.KY.KoreanYoutube.domain.Comment

data class VideoDetailResponseDTO(
    val videoUrl : String,
    val title : String,
//    val description : String,
    val commentList : List<Comment>
)
