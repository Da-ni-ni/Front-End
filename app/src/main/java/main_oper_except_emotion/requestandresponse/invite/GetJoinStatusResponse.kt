package com.example.appdanini.data.model.request.invite

import java.time.LocalDateTime

data class GetJoinStatusResponse(
    val requestId: Long,
    val userName: String,
    val status: RequestStatus,
    val createdAt: String // ISO 8601 형식으로 들어오므로 String으로 받는 게 일반적
)