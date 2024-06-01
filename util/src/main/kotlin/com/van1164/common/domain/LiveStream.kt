package com.van1164.common.domain

import com.van1164.common.dto.StreamDTO
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


@Entity
@Table(name = "live_stream")
data class LiveStream(

    @Column(name = "title")
    var title : String,
    @Column(name = "description")
    var description : String,

    @Column(name = "user_name")
    val userName : String,

    @Column(name = "stream_key")
    val streamKey : String,

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", nullable = false)
    val createdDate : LocalDateTime= LocalDateTime.now(),

    @Column(name = "on_air")
    var onAir : Boolean = false,

    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id : Long? = null,

    @Column(name = "good")
    var good : Int = 0,

    @Column(name = "bad")
    var bad : Int = 0,

    ){
    constructor(streamDTO: StreamDTO, userId : String, streamKey:String):this(streamDTO.title,streamDTO.description,userId,streamKey)
}
