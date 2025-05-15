package main_oper_except_emotion.requestandresponse.emotion

data class EmotionSubmitResponse(
    val emotionId : Long,
    val emotion: EmotionType,
    val createdAt: String
)
