package main_oper_except_emotion.requestandresponse.emotion

data class GetTodayFamilyEmotionListResponse(
    val emotionList: List<PersonalEmotionDetailResponse>
)