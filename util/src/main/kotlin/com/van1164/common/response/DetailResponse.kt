package com.van1164.common.response

import com.van1164.common.domain.VideoR2dbc
import com.van1164.common.dto.CommentDTO

data class DetailResponse(
    val videoR2dbc: VideoR2dbc,
    val comments : List<CommentDTO>,
    val m3U8Url : String
)
