package com.van1164.video.view.repository

import com.van1164.common.domain.VideoView
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoViewRepository : R2dbcRepository<VideoView,Long>{
}