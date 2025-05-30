package com.example.appdanini.data.model.request.invite

import java.time.LocalDateTime

data class GetJoinStatusResponse(
    val requestId: Long,
    val userName: String,
    val status: String,   // ✅ enum이 아닌 문자열로 받기
    val createdAt: String
)