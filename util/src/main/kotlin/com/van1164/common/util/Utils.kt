package com.van1164.common.util

import mu.KotlinLogging
import java.util.UUID


object Utils {
    val logger = KotlinLogging.logger {  }

    fun createFilePath(fileUUID : String, extension : String): String {
        return "$fileUUID.$extension"
    }

    fun createImagePath(): String {
        return UUID.randomUUID().toString() + ".jpg"
    }

    fun createVideoPath(): String {
        return UUID.randomUUID().toString() + ".mp4"
    }

    fun createM3U8Path() : String{
        return UUID.randomUUID().toString() + ".m3u8"
    }
}