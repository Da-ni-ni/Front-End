package main_oper_except_emotion.requestandresponse.diary

data class WeekBoardListResponse(
    val dailyList: List<WeekBoardCheckResponse>
)

data class WeekBoardCheckResponse(
    val dailyId: Long,
    val date: String,
    val content: String,
    val likeCount: Long,
    val commentCount: Long,
    val authorName: String
)