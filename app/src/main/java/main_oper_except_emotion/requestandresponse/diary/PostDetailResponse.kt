package main_oper_except_emotion.requestandresponse.diary

data class PostDetailResponse(
    val dailyId: Long,
    val date: String,
    val authorName: String,
    val content: String,
    val likeCount: Long,
    val commentCount: Long,
    val comments: List<Comment>,
    val userId : Long?
)

data class Comment(
    val commentId: Long,
    val authorName: String,
    val content: String,
    val createdAt: String,
    val userId : Long?
)
