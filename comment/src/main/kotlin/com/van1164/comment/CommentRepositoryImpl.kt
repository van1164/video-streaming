package com.van1164.comment

import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository

@Repository
abstract class CommentRepositoryImpl(
    databaseClient: DatabaseClient
) : CommentRepository {


    fun findById(){

    }
}