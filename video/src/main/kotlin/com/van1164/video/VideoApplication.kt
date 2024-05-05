package com.van1164.video

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@ComponentScan(basePackages = ["com.van1164.common","com.van1164.util","com.van1164.security","com.van1164.video"])
@EnableR2dbcRepositories(basePackageClasses = [VideoR2DBCRepository::class])
@SpringBootApplication
class VideoApplication

fun main(args: Array<String>) {
    runApplication<VideoApplication>(*args)
}
