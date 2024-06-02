package com.van1164.common.domain

import lombok.ToString
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import java.time.LocalDateTime
import java.util.*

//@Entity

data class VideoR2dbc(
    var title : String,
    var url : String,
    val userName : String,
    var thumbNailUrl : String?,
    var good : Int = 0,
    var bad : Int = 0,
    var view : Int = 0,
    @CreatedDate
    val createdDate : LocalDateTime = LocalDateTime.now(),
    @Id
    val id : Long? = null,
)
