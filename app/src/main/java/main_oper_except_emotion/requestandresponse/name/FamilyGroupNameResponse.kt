package main_oper_except_emotion.requestandresponse.name

import java.time.LocalDateTime

data class FamilyGroupNameResponse(
    val group_id : Long,
    val group_name : String,
    val updatedAT : LocalDateTime
)
