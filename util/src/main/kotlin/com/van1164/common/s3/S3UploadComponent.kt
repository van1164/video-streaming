package com.van1164.common.s3

import com.amazonaws.services.s3.model.AmazonS3Exception
import com.van1164.common.util.FileUtils
import com.van1164.common.util.S3Utils
import com.van1164.common.util.Utils.logger
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.io.File

@Component
class S3UploadComponent(
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

    fun uploadVideoPart(video: FilePart, chunkNumber: Int, videoUUID: String): Mono<Boolean> {
        try {
            logger.info("Upload : $chunkNumber")
            return s3Utils.put("$videoUUID.part$chunkNumber",video)
        } catch (e: Exception) {
            logger.error { "업로드 실패 $chunkNumber" }
            logger.error { e.toString() + chunkNumber }
            return Mono.just(false)
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

    fun uploadM3U8(m3u8Path: String, outputUUID: String): Mono<File> {
        println("Upload M3U8")
        return File(m3u8Path).toMono()
            .doOnNext { m3U8File ->
                s3Utils.put(key = "$outputUUID/$m3u8Path",file=m3U8File)
            }
            .doOnNext {m3U8File ->
                fileUtils.delete(m3U8File)
            }

    }

    fun uploadThumbnail(thumbNailPath: String): Mono<File> {
        return File(thumbNailPath).toMono()
            .doOnNext { thumbNailFile ->
                s3Utils.put("thumb/$thumbNailPath", thumbNailFile)
            }.doOnNext{thumbNailFile->
                fileUtils.delete(thumbNailFile)
            }
    }


}