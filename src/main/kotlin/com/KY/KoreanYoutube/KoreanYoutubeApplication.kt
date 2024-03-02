package com.KY.KoreanYoutube

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude= [DataSourceAutoConfiguration::class, SecurityAutoConfiguration::class] )
class KoreanYoutubeApplication

fun main(args: Array<String>) {
	runApplication<KoreanYoutubeApplication>(*args)
}
