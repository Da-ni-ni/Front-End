package com.example.appdanini.data.model.request.invite

import java.time.LocalDateTime

data class GetJoinMyStatusResponse(
    val requestId : Long,
    val userName : String,
    val status : String,
    val createdAt : String,
)
