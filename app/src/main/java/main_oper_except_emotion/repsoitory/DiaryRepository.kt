package main_oper_except_emotion.repsoitory

import main_oper_except_emotion.network.DiaryApi
import main_oper_except_emotion.requestandresponse.diary.CreateCommentRequest
import main_oper_except_emotion.requestandresponse.diary.CreateCommentResponse
import main_oper_except_emotion.requestandresponse.diary.CreateDiaryRequest
import main_oper_except_emotion.requestandresponse.diary.CreateDiaryResponse
import main_oper_except_emotion.requestandresponse.diary.DeleteCommentResponse
import main_oper_except_emotion.requestandresponse.diary.DeleteDiaryResponse
import main_oper_except_emotion.requestandresponse.diary.LikeYouResponse
import main_oper_except_emotion.requestandresponse.diary.PostDetailResponse
import main_oper_except_emotion.requestandresponse.diary.UpdateCommentRequest
import main_oper_except_emotion.requestandresponse.diary.UpdateCommentResponse
import main_oper_except_emotion.requestandresponse.diary.UpdateDiaryRequest
import main_oper_except_emotion.requestandresponse.diary.UpdateDiaryResponse
import main_oper_except_emotion.requestandresponse.diary.WeekBoardCheckResponse
import javax.inject.Inject


class DiaryRepository @Inject constructor(
    private val api: DiaryApi
) {
    suspend fun getWeeklyDiaries(): Result<List<WeekBoardCheckResponse>> = runCatching {
        val response = api.getWeeklyBoards()
        if (response.isSuccessful) {
            Result.success(response.body() ?: emptyList())
        } else {
            Result.failure(Exception("주간 게시글 조회 실패: ${response.code()}"))
        }
    }.getOrElse { Result.failure(it) }



    suspend fun getPostDetail(dailyId: Long): Result<PostDetailResponse> = runCatching {
        val response = api.getPostDetail(dailyId)
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("게시글 상세 조회 실패: ${response.code()}"))
        }
    }.getOrElse { Result.failure(it) }


    suspend fun createDiary(request: CreateDiaryRequest): Result<CreateDiaryResponse> = runCatching {
        val response = api.createDiary(request)
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("게시글 생성 실패: ${response.code()}"))
        }
    }.getOrElse { Result.failure(it) }


    suspend fun updateDiary(dailyId: Long, request: UpdateDiaryRequest): Result<UpdateDiaryResponse> = runCatching {
        val response = api.updateDiary(dailyId, request)
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("게시글 수정 실패: ${response.code()}"))
        }
    }.getOrElse { Result.failure(it) }


    suspend fun deleteDiary(dailyId: Long): Result<DeleteDiaryResponse> = runCatching {
        val response = api.deleteDiary(dailyId)
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("게시글 삭제 실패: ${response.code()}"))
        }
    }.getOrElse { Result.failure(it) }


    suspend fun addComment(dailyId: Long, request: CreateCommentRequest): Result<CreateCommentResponse> = runCatching {
        val response = api.registerComment(dailyId, request)
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("댓글 등록 실패: ${response.code()}"))
        }
    }.getOrElse { Result.failure(it) }


    suspend fun updateComment(dailyId: Long, commentId: Long, request: UpdateCommentRequest): Result<UpdateCommentResponse> = runCatching {
        val response = api.updateComment(dailyId, commentId, request)
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("댓글 수정 실패: ${response.code()}"))
        }
    }.getOrElse { Result.failure(it) }


    suspend fun deleteComment(dailyId: Long, commentId: Long): Result<DeleteCommentResponse> = runCatching {
        val response = api.deleteComment(dailyId, commentId)
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("댓글 삭제 실패: ${response.code()}"))
        }
    }.getOrElse { Result.failure(it) }


    suspend fun toggleDiaryLike(dailyId: Long): Result<LikeYouResponse> = runCatching {
        val response = api.toggleLike(dailyId)
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("좋아요 토글 실패: ${response.code()}"))
        }
    }.getOrElse { Result.failure(it) }
}