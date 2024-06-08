package com.van1164.video

import com.van1164.video.like.VideoLikeRepository
import com.van1164.video.upload.VideoR2DBCRepository
import com.van1164.video.view.repository.VideoViewRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@ComponentScan(basePackages = ["com.van1164.common","com.van1164.util","com.van1164.security","com.van1164.video","com.van1164.main_module","com.van1164.comment"])
@EnableR2dbcRepositories(basePackageClasses = [VideoR2DBCRepository::class,VideoLikeRepository::class,VideoViewRepository::class])
@SpringBootApplication
class VideoApplication

fun main(args: Array<String>) {
    runApplication<VideoApplication>(*args)
}
