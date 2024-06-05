package com.van1164.common.domain

import com.van1164.common.domain.comment.Comment
import jakarta.persistence.*
import lombok.ToString
import java.util.*


@Entity
@Table(name = "sub_comment")
data class SubComment(
    @Column(name = "message")
    val message: String,

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "comment_id")
    val comment: Comment,


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    val createDate : Date,

    @Column(name = "good")
    var good: Int = 0,

    @Column(name = "bad")
    var bad: Int = 0,

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sub_comment_id")
    val id: Long? = null
)
