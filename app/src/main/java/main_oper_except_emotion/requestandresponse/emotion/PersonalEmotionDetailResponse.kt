package main_oper_except_emotion.requestandresponse.emotion

data class PersonalEmotionDetailResponse(
    val emotionId: Long,
    val nickName: String,
    val emotionType: EmotionType,
    val updatedAt: String // LocalDateTime → 문자열로 직렬화된 상태로 받음
)
