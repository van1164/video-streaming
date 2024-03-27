package com.KY.KoreanYoutube.config

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class S3Config(
    @Value("\${aws.s3.accessKey}")
    private val accessKey: String,
    @Value("\${aws.s3.secretKey}")
    private val secretKey: String,
) {
    @Bean
    fun amazonS3Client(): AmazonS3 {
        val clientConfiguration = ClientConfiguration().withMaxErrorRetry(10).withMaxConnections(150)
        return AmazonS3ClientBuilder.standard()
            .withCredentials(
                AWSStaticCredentialsProvider(BasicAWSCredentials(accessKey, secretKey))
            )
            .withRegion(Regions.AP_NORTHEAST_2)
            .withClientConfiguration(clientConfiguration)
            .build()
    }
}