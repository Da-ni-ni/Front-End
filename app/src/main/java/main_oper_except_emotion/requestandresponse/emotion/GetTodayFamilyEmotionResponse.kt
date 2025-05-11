package main_oper_except_emotion.requestandresponse.emotion

data class GetTodayFamilyEmotionResponse(
    val name: String,
    val emotion: Emotion,
    val user_id : Int,
    val nickname: String?,
    val updatedAt: String
)

