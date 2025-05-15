package main_oper_except_emotion.repsoitory

import main_oper_except_emotion.network.QuestionApi
import main_oper_except_emotion.requestandresponse.question.DailyQuestionResponse
import main_oper_except_emotion.requestandresponse.question.DeleteAnswerResponse
import main_oper_except_emotion.requestandresponse.question.MonthlyQuestion
import main_oper_except_emotion.requestandresponse.question.QuestionDetailResponse
import main_oper_except_emotion.requestandresponse.question.SubmitAnswerRequest
import main_oper_except_emotion.requestandresponse.question.SubmitAnswerResponse
import main_oper_except_emotion.requestandresponse.question.UpdateAnswerRequest
import main_oper_except_emotion.requestandresponse.question.UpdateAnswerResponse
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class QuestionRepository @Inject constructor(
    private val api: QuestionApi
) {

    // 1. 오늘의 질문 조회
    suspend fun getDailyQuestion(): Result<DailyQuestionResponse> {
        return try {
            val response = api.getDailyQuestion()
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("응답 본문이 비어 있음"))
            } else {
                Result.failure(Exception("서버 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 2. 답변 제출
    suspend fun submitAnswer(
        questionId: Long,
        request: SubmitAnswerRequest
    ): Result<SubmitAnswerResponse> {
        return try {
            val response = api.submitAnswer(questionId, request)  // questionId와 request를 전달
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("응답 본문이 비어 있음"))
            } else {
                Result.failure(Exception("서버 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 3. 월간 문답 조회
    suspend fun getMonthlyQna(year: Int, month: Int): Result<List<MonthlyQuestion>> {
        return try {
            val response = api.getMonthlyQna(year, month)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("응답 본문이 비어 있음"))
            } else {
                Result.failure(Exception("서버 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 4. 답변 수정 (PUT)
    suspend fun updateAnswer(
        questionId: Long,
        request: UpdateAnswerRequest
    ): Result<UpdateAnswerResponse> {
        return try {
            val response = api.updateAnswer(questionId, request)  // questionId와 request를 전달
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("응답 본문이 비어 있음"))
            } else {
                Result.failure(Exception("서버 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 5. 답변 삭제 (DELETE)
    suspend fun deleteAnswer(questionId: Long): Result<DeleteAnswerResponse> {
        return try {
            val response = api.deleteAnswer(questionId)  // questionId를 전달
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("응답 본문이 비어 있음"))
            } else {
                Result.failure(Exception("서버 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 6. 질문 상세 조회
//    suspend fun getQuestionDetail(questionId: Int): Result<QuestionDetailResponse> {
//        return try {
//            val response = api.getQuestionDetail(questionId)
//            if (response.isSuccessful) {
//                response.body()?.let { Result.success(it) }
//                    ?: Result.failure(Exception("응답 본문이 비어 있음"))
//            } else {
//                Result.failure(Exception("서버 오류: ${response.code()}"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//}
    suspend fun getQuestionDetail(questionId: Long): Result<QuestionDetailResponse> {
        return try {
            val response = api.getQuestionDetail(questionId)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("응답 본문이 비어 있음"))
            } else {
                Result.failure(Exception("서버 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



}