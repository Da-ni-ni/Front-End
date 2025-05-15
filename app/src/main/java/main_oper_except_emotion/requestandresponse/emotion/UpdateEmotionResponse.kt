package main_oper_except_emotion.requestandresponse.emotion

import java.time.LocalDateTime

// 수정 필요
data class UpdateEmotionResponse(
    val nickName : String?,
    val emotion : EmotionType,
    val emotionId : Long,
    val updatedAt : LocalDateTime
)
