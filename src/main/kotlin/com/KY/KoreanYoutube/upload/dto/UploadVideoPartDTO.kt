package com.KY.KoreanYoutube.upload.dto

data class UploadVideoPartDTO(
    val title : String,
    val chunkNumber : Int,
    val totalChunk : Int,
)