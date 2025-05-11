package main_oper_except_emotion.requestandresponse.diary

data class Comment(
    val comment_id: Int,
    val name: String,
    val nickname : String?,// = nickname
    val comment: String,
    // 고려 필요
    val time: String
)

data class PostDetailResponse(
    val daily_id: Int,
    val date: String,
    val content: String,
    val like_count: Int,
    val comment_count: Int,
    val name: String,        // 작성자 닉네임
    val nickname: String?,
    val time: String,
    val comments: List<Comment> // List<Comment>로 수정
)
