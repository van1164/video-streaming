package com.KY.KoreanYoutube.upload

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.*
import mu.KotlinLogging
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileInputStream

private val logger = KotlinLogging.logger {} // KotlinLogging 사용
@Repository
class UploadRepository(
    val amazonS3: AmazonS3
) {

    fun uploadVideoTs(tsFilePath: String){
        var count = 0
        while(true){
            val tsPath = tsFilePath + "_" + count.toString().padStart(3,'0') +".ts"
            val tsFile = File(tsPath)
            if (tsFile.isFile){
                amazonS3.putObject(
                    PutObjectRequest(
                        "video-stream-spring",
                        tsPath,
                        tsFile.inputStream(),
                        ObjectMetadata()
                    )
                )
                tsFile.delete()
                count++
            }
            else{
                break
            }
        }

    }

    fun uploadVideoTsVer2(tsPath: String, tsFileInputStream : FileInputStream){
        amazonS3.putObject(
            PutObjectRequest(
                "video-stream-spring",
                tsPath,
                tsFileInputStream,
                ObjectMetadata()
            )
        )
    }

    fun uploadVideoPart(video: MultipartFile, chunkNumber: Int): Boolean {
        try {
            println("Upload : $chunkNumber")
            amazonS3.putObject(
                PutObjectRequest(
                    "video-stream-spring",
                    video.originalFilename + ".part" + chunkNumber,
                    video.inputStream,
                    ObjectMetadata()
                )
            )
            return true
        } catch (e : Exception){
            return false
        }

    }

    fun getPart(bucketUrl: String, originalFilename: String?, i: Int): S3ObjectInputStream? {
        logger.info("getPart : $i")
        return try {
            amazonS3.getObject(
                "video-stream-spring",
                "$originalFilename.part$i"
            ).use{
                return it.objectContent
            }

        }
        catch (e: AmazonS3Exception){
            null
        }
    }

    fun getPartByteArray(bucketUrl: String, originalFilename: String?, i: Int): ByteArray? {
        logger.info("getPart : $i")
        return try {
            amazonS3.getObject(
                "video-stream-spring",
                "$originalFilename.part$i"
            ).use{
                return it.objectContent.readAllBytes()
            }

        }
        catch (e: AmazonS3Exception){
            null
        }
    }


    fun deletePart(originalFilename: String?, i: Int){
        logger.info("deletePart : $i")
        amazonS3.deleteObject("video-stream-spring","$originalFilename.part$i" )
    }

    fun uploadM3U8(m3u8Path: String) {
        println("Upload M3U8")
        val m3u8File = File(m3u8Path)
        amazonS3.putObject(
            PutObjectRequest(
                "video-stream-spring",
                m3u8Path,
                m3u8File.inputStream(),
                ObjectMetadata()
            )
        )
        m3u8File.delete()
    }

    fun uploadThumbnail(thumbNailPath: String) {
        val thumbNailFile = File(thumbNailPath)
        amazonS3.putObject(
            PutObjectRequest(
                "video-stream-spring",
                thumbNailPath,
                thumbNailFile.inputStream(),
                ObjectMetadata()
            )
        )
        thumbNailFile.delete()
    }

}