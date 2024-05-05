package com.van1164.common.util

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.model.S3Object
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import java.io.File
import java.io.InputStream

@Component(value = "s3Utils")
class S3Utils(
    val amazonS3: AmazonS3,
    @Value("\${aws.s3.bucketName}")
    val bucketName : String,
) {
     fun put(key: String, file: File) {
        val request = PutObjectRequest(
            bucketName,
            key,
            file
        )
        request.requestClientOptions.readLimit = 3000
        amazonS3.putObject(request)
    }

    fun put(
        key: String,
        video: MultipartFile
    ) {
        val request = PutObjectRequest(
            bucketName,
            key,
            video.inputStream,
            ObjectMetadata().apply {
                contentLength = video.size
            }
        )
        request.requestClientOptions.readLimit = 80000
        amazonS3.putObject(request)
    }

    fun put(
        key: String,
        video: FilePart
    ): Mono<Boolean> {
        return DataBufferUtils.join(video.content()).map {
            val request = PutObjectRequest(
                bucketName,
                key,
                it.asInputStream(),
                ObjectMetadata()
            )
            request.requestClientOptions.readLimit = 80000
            request
            }
            .doOnNext {request->
                amazonS3.putObject(request)
            }
            .map {
                true
            }
    }


    fun get(key : String): S3Object {
        return amazonS3.getObject(
            bucketName,
            key
        )
    }

    fun delete(key : String) {
        println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX$bucketName")
        amazonS3.deleteObject(
            bucketName,
            key
        )
    }


}