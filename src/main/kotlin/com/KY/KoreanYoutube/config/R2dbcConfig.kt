package com.KY.KoreanYoutube.config

import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Import(DataSourceAutoConfiguration::class)
@Configuration
class R2dbcConfig {
}