package main_oper_except_emotion

data class WeekBoardUiModel(
    val dailyId: Long,
    val date: String,      // yyyy-MM-dd
    val time: String,      // 오후 2:22
    val authorName: String,
    val content: String,
    val likeCount: Long,
    val commentCount: Long
)