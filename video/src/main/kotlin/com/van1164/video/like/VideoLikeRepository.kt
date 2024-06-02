package com.van1164.video.like

import com.van1164.common.domain.VideoLike
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoLikeRepository : R2dbcRepository<VideoLike,Long> {

}