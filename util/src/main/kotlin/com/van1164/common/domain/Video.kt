package com.van1164.common.domain

import com.van1164.common.domain.Comment
import jakarta.persistence.*
import lombok.ToString
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Transient
import java.util.*

@Entity
@Table(name = "video")
data class Video(

    @Column(name = "title")
    var title : String,

    @Id
    @org.springframework.data.annotation.Id
    @Column(name = "video_id")
    var id : String,

    @Column(name = "user_id")
    val userName : String,

    @Column(name = "thumbnail_url")
    var thumbNailUrl : String?,



    @Column(name = "comments")
    @ToString.Exclude
    @OneToMany(mappedBy = "id", fetch = FetchType.LAZY)
    @Transient
    val commentList : MutableList<Comment> = mutableListOf(),


    @Column
    var good : Int = 0,

    @Column
    var bad : Int = 0,

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    val createDate : Date = Date.from(Date().toInstant()),
)
