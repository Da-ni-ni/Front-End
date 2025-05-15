package main_oper_except_emotion.requestandresponse.question

import java.time.LocalDateTime

data class UpdateAnswerResponse(
    val questionId : Long,
    val userId : Long,
    val answer : String,
    val updatedAt : LocalDateTime
)
