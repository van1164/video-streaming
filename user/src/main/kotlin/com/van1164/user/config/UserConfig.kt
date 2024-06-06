package com.van1164.user.config

import com.van1164.user.UserRepository
import com.van1164.user.subscribe.repository.SubscribeRepository
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories


@Configuration
@EnableR2dbcRepositories(basePackageClasses = [SubscribeRepository::class,UserRepository::class])
class UserConfig {
}