package main_oper_except_emotion.requestandresponse.question

data class UpdateAnswerResponse(
    val question_id : Int,
    val user_id : Int,
    val answer : String,
    val updatedAt : String
)
