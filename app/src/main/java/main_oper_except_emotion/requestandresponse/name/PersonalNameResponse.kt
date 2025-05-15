package main_oper_except_emotion.requestandresponse.name

import java.time.LocalDateTime

data class PersonalNameResponse(
    val emotionId : Long,
    val nickName : Long?,
    val name: String,
    val updatedAt : LocalDateTime
)
