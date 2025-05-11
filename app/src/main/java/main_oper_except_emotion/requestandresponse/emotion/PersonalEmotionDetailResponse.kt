package main_oper_except_emotion.requestandresponse.emotion

data class PersonalEmotionDetailResponse(
    val user_id : Int,
    val name : String,
    val emotion : Emotion,
    val updatedAt : String
)
