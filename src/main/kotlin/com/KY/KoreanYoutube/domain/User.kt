package com.KY.KoreanYoutube.domain

import jakarta.persistence.*
import java.util.*


@Entity
@Table(name = "user")
data class User(

    @Column(name = "name")
    val name: String,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    val createDate : Date,

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    val id: Long? = null,

    @Column(name = "on_air")
    var onAir: Boolean = false,
)
