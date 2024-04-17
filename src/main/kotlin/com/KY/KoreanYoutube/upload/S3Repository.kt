package com.KY.KoreanYoutube.upload

import com.KY.KoreanYoutube.utils.FileUtils
import com.KY.KoreanYoutube.utils.S3Utils
import com.amazonaws.services.s3.model.*
import mu.KotlinLogging
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import java.io.File

private val logger = KotlinLogging.logger {} // KotlinLogging 사용

@Repository
class S3Repository(
    val fileUtils: FileUtils,
    val s3Utils: S3Utils
) {
    fun uploadVideoTs(tsPath: String, tsFile: File) {
        try {
            s3Utils.put(tsPath, tsFile)
        } catch (e: Exception) {
            logger.error { e.toString() }
        }

    }

    fun uploadVideoPart(video: MultipartFile, chunkNumber: Int, videoUUID: String): Boolean {
        try {
            logger.info("Upload : $chunkNumber")
            s3Utils.put("$videoUUID.part$chunkNumber", video)
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
            s3Utils.get(
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
        s3Utils.delete("$videoUUID.part$i")
    }

    fun uploadM3U8(m3u8Path: String, outputUUID: String) {
        println("Upload M3U8")
        val m3u8File = File(m3u8Path)
        s3Utils.put(key = "$outputUUID/$m3u8Path", file = m3u8File)
        fileUtils.delete(m3u8File)
    }

    fun uploadThumbnail(thumbNailPath: String) {
        val thumbNailFile = File(thumbNailPath)
        s3Utils.put("thumb/$thumbNailPath", thumbNailFile)
        fileUtils.delete(thumbNailFile)
    }


}