package main_oper_except_emotion.requestandresponse.question

import main_oper_except_emotion.Answer

data class QuestionDetailResponse(
    val questionId: Long,
    val date: String,
    val dailyQuestion: String,
    val answers: List<Answer>
)

data class UserAnswer(
    val userId: Long,
    val nickname: String,
    val answer: String? // null이면 미답변
)