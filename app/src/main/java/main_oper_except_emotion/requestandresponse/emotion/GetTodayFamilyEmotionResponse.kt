package main_oper_except_emotion.requestandresponse.emotion

data class GetTodayFamilyEmotionResponse(
    val name: String,
    val emotion: EmotionType,
    val emotionId : Long,
    val nickname: String?,
    val updatedAt: String
)

