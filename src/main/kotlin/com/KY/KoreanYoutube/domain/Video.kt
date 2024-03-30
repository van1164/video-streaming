package com.KY.KoreanYoutube.domain

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "video")
data class Video(

    @Column(name = "title")
    var title : String,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    val createDate : Date,

    @Id
    var id : String,

    @Column(name = "thumbnail_url")
    var thumbNailUrl : String?,
)
