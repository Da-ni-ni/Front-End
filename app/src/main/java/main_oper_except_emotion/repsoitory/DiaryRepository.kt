package main_oper_except_emotion.repsoitory

import main_oper_except_emotion.network.DiaryApi
import main_oper_except_emotion.requestandresponse.diary.CreateCommentRequest
import main_oper_except_emotion.requestandresponse.diary.CreateDiaryRequest
import main_oper_except_emotion.requestandresponse.diary.UpdateCommentRequest
import main_oper_except_emotion.requestandresponse.diary.UpdateDiaryRequest
import javax.inject.Inject

class DiaryRepository @Inject constructor(
    private val api: DiaryApi
) {
    suspend fun fetchWeeklyDiaries() = api.getWeeklyBoards()

    suspend fun getPostDetail(dailyId: Int) = api.getPostDetail(dailyId)

    suspend fun createDiary(request: CreateDiaryRequest) = api.createDiary(request)

    suspend fun updateDiary(dailyId: Int, request: UpdateDiaryRequest) = api.updateDiary(dailyId, request)

    suspend fun deleteDiary(dailyId: Int) = api.deleteDiary(dailyId)

    suspend fun addComment(dailyId: Int, request: CreateCommentRequest) = api.registerComment(dailyId, request)


    suspend fun updateComment(dailyId: Int, commentId: Int, request: UpdateCommentRequest) =
        api.updateComment(dailyId, commentId, request)

    suspend fun deleteComment(dailyId: Int, commentId: Int) =
        api.deleteComment(dailyId, commentId)

    suspend fun toggleDiaryLike(dailyId: Int) = api.toggleLike(dailyId)
}
