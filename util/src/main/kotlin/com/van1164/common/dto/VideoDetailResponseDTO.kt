package com.van1164.common.dto

import com.van1164.common.domain.comment.Comment


data class VideoDetailResponseDTO(
    val videoUrl : String,
    val title : String,
//    val description : String,
    val commentList : List<Comment>
)
