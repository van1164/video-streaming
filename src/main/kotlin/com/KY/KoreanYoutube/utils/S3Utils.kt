package com.KY.KoreanYoutube.utils

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.model.S3Object
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Component(value = "s3Utils")
class S3Utils(
    val amazonS3: AmazonS3
) {
     fun put(key: String, file: File) {
        val request = PutObjectRequest(
            BUCKET_NAME,
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
            BUCKET_NAME,
            key,
            video.inputStream,
            ObjectMetadata().apply {
                contentLength = video.size
            }
        )
        request.requestClientOptions.readLimit = 80000
        amazonS3.putObject(request)
    }

    fun get(key : String): S3Object {
        return amazonS3.getObject(
            BUCKET_NAME,
            key
        )
    }

    fun delete(key : String) {
        amazonS3.deleteObject(
            BUCKET_NAME,
            key
        )
    }


}