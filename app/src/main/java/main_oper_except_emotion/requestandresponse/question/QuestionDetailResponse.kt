package main_oper_except_emotion.requestandresponse.question

import main_oper_except_emotion.Answer

data class QuestionDetailResponse(
    val question_id: Int,
    val date: String,
    val daily_question: String,
    val answers: List<Answer>
)

data class UserAnswer(
    val user_id: Long,
    val nickname: String,
    val answer: String? // null이면 미답변
)