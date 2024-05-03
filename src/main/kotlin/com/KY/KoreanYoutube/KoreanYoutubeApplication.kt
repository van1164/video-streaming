package com.KY.KoreanYoutube

import com.KY.KoreanYoutube.stream.StreamRepository
import com.KY.KoreanYoutube.user.UserR2DBCRepository
import com.KY.KoreanYoutube.user.UserRepository
import com.KY.KoreanYoutube.video.VideoR2DBCRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@EnableJpaRepositories(basePackageClasses = [StreamRepository::class,UserRepository::class] )
@EnableR2dbcRepositories(basePackageClasses = [VideoR2DBCRepository::class, UserR2DBCRepository::class])
@EnableR2dbcAuditing
@SpringBootApplication
class KoreanYoutubeApplication

fun main(args: Array<String>) {
	runApplication<KoreanYoutubeApplication>(*args)
}
