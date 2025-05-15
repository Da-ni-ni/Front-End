package main_oper_except_emotion.repsoitory

import com.example.appdanini.data.model.request.closeness.ClosenessAnswerRequest
import com.example.appdanini.data.model.request.closeness.ClosenessScoreResponse
import main_oper_except_emotion.network.ClosenessApi
import retrofit2.Response
import javax.inject.Inject

class ClosenessRepository @Inject constructor(
    private val closenessApi: ClosenessApi
) {
    suspend fun submitAnswers(request: ClosenessAnswerRequest): Response<Unit> {
        return closenessApi.submitAnswers(request)
    }

    suspend fun getClosenessScores(): Response<ClosenessScoreResponse> {
        return closenessApi.getClosenessScores()
    }
}
