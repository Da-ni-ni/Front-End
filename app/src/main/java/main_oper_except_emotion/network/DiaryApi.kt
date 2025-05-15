package main_oper_except_emotion.network

import main_oper_except_emotion.requestandresponse.diary.UpdateCommentResponse
import main_oper_except_emotion.requestandresponse.diary.CreateCommentRequest
import main_oper_except_emotion.requestandresponse.diary.CreateCommentResponse
import main_oper_except_emotion.requestandresponse.diary.CreateDiaryRequest
import main_oper_except_emotion.requestandresponse.diary.CreateDiaryResponse
import main_oper_except_emotion.requestandresponse.diary.DeleteCommentResponse
import main_oper_except_emotion.requestandresponse.diary.DeleteDiaryResponse
import main_oper_except_emotion.requestandresponse.diary.PostDetailResponse
import main_oper_except_emotion.requestandresponse.diary.UpdateCommentRequest
import main_oper_except_emotion.requestandresponse.diary.UpdateDiaryRequest
import main_oper_except_emotion.requestandresponse.diary.UpdateDiaryResponse
import main_oper_except_emotion.requestandresponse.diary.WeekBoardCheckResponse
import main_oper_except_emotion.requestandresponse.diary.LikeYouResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DiaryApi {

    // 주간 게시글 조회
    // 데이터 클래스 한번 더 확인하기
    @Headers("Need-Auth: true")
    @GET("/api/v1/daily")
    suspend fun getWeeklyBoards(): Response<List<WeekBoardCheckResponse>>

    // 게시글 상세 조회
    @Headers("Need-Auth: true")
    @GET("/api/v1/daily/{dailyId}")
    suspend fun getPostDetail(@Path("dailyId") id: Long): Response<PostDetailResponse>

    @Headers("Need-Auth: true")
    @POST("/api/v1/daily")
    suspend fun createDiary(
    @Body request: CreateDiaryRequest
    ): Response<CreateDiaryResponse>

    // 일기 수정
    @Headers("Need-Auth: true")
    @PUT("/api/v1/daily/{dailyId}")
    suspend fun updateDiary(
        @Path("dailyId") dailyId: Long,
        @Body request: UpdateDiaryRequest
    ): Response<UpdateDiaryResponse>

    // 일기 삭제
    @Headers("Need-Auth: true")
    @DELETE("/api/v1/daily/{dailyId}")
    suspend fun deleteDiary(
        @Path("dailyId") dailyId: Long):Response<DeleteDiaryResponse>

    // 댓글 등록
    @Headers("Need-Auth: true")
    @POST("/api/v1/daily/{dailyId}/comments")
    suspend fun registerComment(
        @Path("dailyId") dailyId : Long,
        @Body request : CreateCommentRequest
    ): Response<CreateCommentResponse>

    // 댓글 수정
    @Headers("Need-Auth: true")
    @PUT("/api/v1/daily/{dailyId}/comments/{commentId}")
    suspend fun updateComment(
        @Path("dailyId") dailyId : Long,
        @Path("commentId") commentId : Long,
        @Body request : UpdateCommentRequest
    ): Response<UpdateCommentResponse>

    // 댓글 삭제
    @Headers("Need-Auth: true")
    @DELETE("/api/v1/daily/{dailyId}/comments/{commentId}")
    suspend fun deleteComment(
        @Path("dailyId") dailyId : Long,
        @Path("commentId") commentId : Long,
    ) : Response<DeleteCommentResponse>

    // 좋아요 토글
    @Headers("Need-Auth: true")
    @POST("/api/v1/daily/{dailyId}")
    suspend fun toggleLike(@Path("dailyId") dailyId: Long): Response<LikeYouResponse>
}
