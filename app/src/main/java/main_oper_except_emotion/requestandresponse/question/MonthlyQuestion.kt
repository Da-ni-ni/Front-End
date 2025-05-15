package main_oper_except_emotion.requestandresponse.question

import java.time.LocalDate

data class MonthlyQuestion(
    val questionId: Long,
    val question: String,
    val date: String
)
