package main_oper_except_emotion.network

import com.example.appdanini.data.model.request.closeness.ClosenessAnswerRequest
import com.example.appdanini.data.model.request.closeness.ClosenessScoreResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

//Retrofit 통신용 API가 아니라 "Repository랑 ViewModel 연결하기 위한 추상 인터페이스" 였어

interface ClosenessApi {

    @Headers("Need-Auth: true")
    @POST("/api/v1/tests/answers")
    suspend fun submitAnswers(
        @Body request: ClosenessAnswerRequest
    ): Response<Unit>

    @Headers("Need-Auth: true")
    @GET("/api/v1/tests")
    suspend fun getClosenessScores(): Response<ClosenessScoreResponse>
}
