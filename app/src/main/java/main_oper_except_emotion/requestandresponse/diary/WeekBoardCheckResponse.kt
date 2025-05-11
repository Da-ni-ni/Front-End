package main_oper_except_emotion.requestandresponse.diary

data class WeekBoardCheckResponse(
    val daily_id : Int,
    val date : String,
    val content : String,
    val like_count : Int,
    val comment_count : Int,

    // 필요하다고 생각함
    val name : String,
    val time: String
)
