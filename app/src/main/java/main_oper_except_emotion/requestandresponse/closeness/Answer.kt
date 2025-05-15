package com.example.appdanini.data.model.request.closeness

data class Answer(
    val questionId: Int,
    val groupId: Long,
    val answer: Int // 또는 String 등, 점수 형태면 Int
)