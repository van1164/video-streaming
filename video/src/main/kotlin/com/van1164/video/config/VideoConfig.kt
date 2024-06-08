package com.van1164.video.config

import com.van1164.video.like.VideoLikeRepository
import com.van1164.video.video.VideoRepository
import com.van1164.video.view.repository.VideoViewRepository
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@Configuration
@EnableR2dbcRepositories(basePackageClasses = [VideoRepository::class, VideoLikeRepository::class, VideoViewRepository::class])
class VideoConfig {
}