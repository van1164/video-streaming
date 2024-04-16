package com.KY.KoreanYoutube.upload

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.*
import mu.KotlinLogging
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import java.io.File

private val logger = KotlinLogging.logger {} // KotlinLogging 사용

@Repository
class UploadRepository(
    val amazonS3: AmazonS3
) {
    fun uploadVideoTs(tsPath: String, tsFile: File) {
        try {
            putObject(tsPath, tsFile)

        } catch (e: Exception) {
            logger.error { e.toString() }
        }

    }

    fun uploadVideoPart(video: MultipartFile, chunkNumber: Int, videoUUID: String): Boolean {
        try {
            logger.info("Upload : $chunkNumber")
            putObjectStream("$videoUUID.part$chunkNumber", video)
            return true
        } catch (e: Exception) {
            logger.error { "업로드 실패 $chunkNumber" }
            logger.error { e.toString() + chunkNumber }
            return false
        }

    }

    fun getPartByteArray(bucketUrl: String, videoUUID: String, i: Int): ByteArray? {
        logger.info("getPart : $i")
        return try {
            amazonS3.getObject(
                "video-stream-spring",
                "$videoUUID.part$i"
            ).use {
                return it.objectContent.readAllBytes()
            }

        } catch (e: AmazonS3Exception) {
            null
        }
    }


    fun deletePart(videoUUID: String, i: Int) {
        logger.info("deletePart : $i")
        amazonS3.deleteObject("video-stream-spring", "$videoUUID.part$i")
    }

    fun uploadM3U8(m3u8Path: String, outputUUID: String) {
        println("Upload M3U8")
        val m3u8File = File(m3u8Path)
        putObject(key = "$outputUUID/$m3u8Path", file = m3u8File)
        fileDelete(m3u8File)
    }

    fun uploadThumbnail(thumbNailPath: String) {
        val thumbNailFile = File(thumbNailPath)
        putObject(thumbNailPath, thumbNailFile)
        fileDelete(thumbNailFile)
    }

    private fun putObject(key: String, file: File) {
        val request = PutObjectRequest(
            "video-stream-spring",
            key,
            file
        )
        request.requestClientOptions.readLimit = 3000
        amazonS3.putObject(request)
    }

    private fun putObjectStream(
        key: String,
        video: MultipartFile
    ) {
        val request = PutObjectRequest(
            "video-stream-spring",
            key,
            video.inputStream,
            ObjectMetadata().apply {
                contentLength = video.size
            }
        )
        request.requestClientOptions.readLimit = 80000
        amazonS3.putObject(request)
    }

    private fun fileDelete(file:File){
        if (!file.delete()) {
            throw IllegalStateException()
        }
    }


}