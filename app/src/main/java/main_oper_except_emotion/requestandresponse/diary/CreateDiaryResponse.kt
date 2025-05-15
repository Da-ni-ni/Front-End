package main_oper_except_emotion.requestandresponse.diary

import java.time.LocalDateTime

data class CreateDiaryResponse(
    val dailyId : Long,
    val createdAt : String

)
