package com.van1164.common.dto

data class UploadVideoPartDTO(
    val title : String,
    val chunkNumber : Int,
    val totalChunk : Int,
    val fileUUID : String,
)