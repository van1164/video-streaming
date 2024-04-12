package com.KY.KoreanYoutube.domain

import com.KY.KoreanYoutube.dto.StreamDTO
import jakarta.persistence.*
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

    @Column(name = "user_id")
    val userId : Long,

    @Column(name = "stream_key")
    val streamKey : String,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    val createDate : Date= Date.from(
        LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
    ),

    @Column(name = "on_air")
    val onAir : Boolean = true,

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id : Long? = null,

    @Column(name = "good")
    var good : Int = 0,

    @Column(name = "bad")
    var bad : Int = 0,

    ){
    constructor(streamDTO: StreamDTO, streamKey:String):this(streamDTO.title,streamDTO.description,streamDTO.userId,streamKey)
}
