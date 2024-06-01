package com.van1164.common.response

import com.van1164.common.domain.LiveStream
import com.van1164.common.domain.VideoR2dbc

data class MainResponse (
    var videoList : List<VideoR2dbc> = listOf(),
    var liveList : List<LiveStream> = listOf(),
)
