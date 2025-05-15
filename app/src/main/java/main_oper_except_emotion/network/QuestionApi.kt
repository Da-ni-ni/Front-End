package main_oper_except_emotion.network
import main_oper_except_emotion.requestandresponse.question.DailyQuestionResponse
import main_oper_except_emotion.requestandresponse.question.DeleteAnswerResponse
import main_oper_except_emotion.requestandresponse.question.MonthlyQuestion
import main_oper_except_emotion.requestandresponse.question.QuestionDetailResponse
import main_oper_except_emotion.requestandresponse.question.SubmitAnswerRequest
import main_oper_except_emotion.requestandresponse.question.SubmitAnswerResponse
import main_oper_except_emotion.requestandresponse.question.UpdateAnswerRequest
import main_oper_except_emotion.requestandresponse.question.UpdateAnswerResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

    interface QuestionApi {

        // 1. 오늘의 질문 조회
        @Headers("Need-Auth: true")
        @GET("/api/v1/question/everyday")
        suspend fun getDailyQuestion(): Response<DailyQuestionResponse>

        // 2. 답변 제출
        @Headers("Need-Auth: true")
        @POST("/api/v1/question/{questionId}/answers")
        suspend fun submitAnswer(
            @Path("questionId") questionId: Long,  // questionId가 Path로 전달
            @Body request: SubmitAnswerRequest
        ): Response<SubmitAnswerResponse>

        // 3. 월간 문답 리스트 조회
        @Headers("Need-Auth: true")
        @GET("/api/v1/question/monthly")
        suspend fun getMonthlyQna(
            @Query("year") year: Int,
            @Query("month") month: Int
        ): Response<List<MonthlyQuestion>>

        // 4. 상세 조회
// 🔄 문답 상세 조회
        @Headers("Need-Auth: true")
        @GET("/api/v1/question/{questionId}")
        suspend fun getQuestionDetail(
            @Path("questionId") questionId: Long
        ): Response<QuestionDetailResponse>

        // 5. 답변 수정 (PUT)
        @Headers("Need-Auth: true")
        @PUT("/api/v1/question/{questionId}/answers")
        suspend fun updateAnswer(
            @Path("questionId") questionId: Long,
            @Body request: UpdateAnswerRequest
        ): Response<UpdateAnswerResponse>

        // 6. 답변 삭제 (DELETE)
        @Headers("Need-Auth: true")
        @DELETE("/api/v1/question/{questionId}/answers")
        suspend fun deleteAnswer(
            @Path("questionId") questionId: Long
        ): Response<DeleteAnswerResponse>
    }
