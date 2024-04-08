package com.KY.KoreanYoutube.domain

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*
import lombok.ToString
import java.util.*

@Entity
@Table(name = "comment")
data class Comment(

    @Column(name = "message")
    val message : String,

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "video_id")
    val video: Video,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    val createDate : Date,

    @Column(name = "good")
    var good : Int = 0,

    @Column(name = "bad")
    var bad : Int = 0,


    @OneToMany(mappedBy = "id", fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonBackReference
    var subComments : MutableList<SubComment> = mutableListOf(),


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "comment_id")
    val id : Long? = null
)
