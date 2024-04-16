package com.KY.KoreanYoutube.domain

import jakarta.persistence.*
import lombok.ToString
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
    @Column(name = "video_id")
    var id : String,

    @Column(name = "user_id")
    val userName : String,

    @Column(name = "thumbnail_url")
    var thumbNailUrl : String?,



    @Column(name = "comments")
    @ToString.Exclude
    @OneToMany(mappedBy = "id", fetch = FetchType.EAGER)
    val commentList : MutableList<Comment> = mutableListOf(),


    @Column
    var good : Int = 0,

    @Column
    var bad : Int = 0,
)
