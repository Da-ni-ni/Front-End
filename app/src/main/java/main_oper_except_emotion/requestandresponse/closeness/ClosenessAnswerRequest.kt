package com.example.appdanini.data.model.request.closeness

// 답변을 서버로 보낼 때 사용하는 데이터
data class ClosenessAnswerRequest(
    val answers: List<Answer>
)