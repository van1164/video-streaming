package com.KY.KoreanYoutube.dto

data class UploadVideoPartDTO(
    val title : String,
    val chunkNumber : Int,
    val totalChunk : Int,
    val fileUUID : String,
)