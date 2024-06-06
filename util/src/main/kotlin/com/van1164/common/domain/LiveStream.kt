package com.van1164.common.domain

import com.van1164.common.dto.StreamDTO
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import java.time.LocalDateTime


data class LiveStream(
    var title : String,
    var description : String,
    val userName : String,
    val streamKey : String,

    @CreatedDate
    val createdDate : LocalDateTime = LocalDateTime.now(),

    var onAir : Boolean = false,

    @Id
    val id : Long? = null,

    var good : Int = 0,

    var bad : Int = 0,

    ){
    constructor(streamDTO: StreamDTO, userId : String, streamKey:String):this(streamDTO.title,streamDTO.description,userId,streamKey)
}
