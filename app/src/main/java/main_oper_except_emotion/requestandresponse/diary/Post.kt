
package main_oper_except_emotion.requestandresponse.diary

data class Post(
    val id: Int,             // 글의 고유 ID
    val title: String,       // 글의 제목
    val content: String,     // 글 내용
    val author: String,      // 작성자
    val timestamp: String    // 작성 시간
)