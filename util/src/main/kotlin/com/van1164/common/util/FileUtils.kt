package com.van1164.common.util

import org.springframework.stereotype.Component
import java.io.File

@Component(value = "fileUtils")
class FileUtils {
    fun delete(file: File){
        if (!file.delete()) {
            throw IllegalStateException()
        }
    }
}