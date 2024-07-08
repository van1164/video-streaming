package com.van1164.common.domain

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import lombok.ToString
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import java.time.LocalDateTime
import java.util.*

//@Entity

data class VideoR2dbc(
    @NotBlank
    var title : String,
    @NotBlank
    var url : String,
    @NotBlank
    val userName : String,

    var thumbNailUrl : String?,
    @NotNull
    @Min(0)
    var good : Int = 0,
    @NotNull
    @Min(0)
    var bad : Int = 0,
    @NotNull
    @Min(0)
    var view : Int = 0,

    @NotNull
    @CreatedDate
    val createdDate : LocalDateTime = LocalDateTime.now(),

    @NotNull
    @Id
    val id : Long? = null,
)
