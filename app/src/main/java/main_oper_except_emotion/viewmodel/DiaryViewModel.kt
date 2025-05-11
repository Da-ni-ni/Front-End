package main_oper_except_emotion.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import main_oper_except_emotion.repsoitory.DiaryRepository
import main_oper_except_emotion.requestandresponse.diary.CreateDiaryRequest
import main_oper_except_emotion.requestandresponse.diary.UpdateDiaryRequest
import main_oper_except_emotion.requestandresponse.diary.CreateCommentRequest
import main_oper_except_emotion.requestandresponse.diary.CreateDiaryResponse
import main_oper_except_emotion.requestandresponse.diary.PostDetailResponse
import main_oper_except_emotion.requestandresponse.diary.UpdateCommentRequest
import main_oper_except_emotion.requestandresponse.diary.WeekBoardCheckResponse
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val repository: DiaryRepository
) : ViewModel() {

    private val _weeklyBoards = MutableStateFlow<List<WeekBoardCheckResponse>>(emptyList())
    val weeklyBoards: StateFlow<List<WeekBoardCheckResponse>> = _weeklyBoards

    private val _postDetail = MutableStateFlow<PostDetailResponse?>(null)
    val postDetail: StateFlow<PostDetailResponse?> = _postDetail

    private val _diaryCreated = MutableLiveData<Boolean>()
    val diaryCreated: LiveData<Boolean> get() = _diaryCreated

    private val _diaryCreatedResponse = MutableLiveData<CreateDiaryResponse>()
    val diaryCreatedResponse: LiveData<CreateDiaryResponse> get() = _diaryCreatedResponse

    private val _diaryUpdated = MutableLiveData<Boolean>()
    val diaryUpdated: LiveData<Boolean> get() = _diaryUpdated

    private val _diaryDeleted = MutableStateFlow<Boolean?>(null)
    val diaryDeleted: StateFlow<Boolean?> = _diaryDeleted

    private val _likeToggled = MutableLiveData<Boolean>()
    val likeToggled: LiveData<Boolean> get() = _likeToggled


    // 주간 게시글 가져오기
    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchWeeklyDiaries(selectedStartDate: LocalDate) {
        viewModelScope.launch {
            val response = repository.fetchWeeklyDiaries() // 실제 API 호출
            if (response.isSuccessful) {
                val fullList = response.body() ?: emptyList()

                _weeklyBoards.value = fullList.filter { board ->
                    // 게시글의 날짜가 선택된 날짜(주간 범위 내에 있는지)와 일치하는지 확인
                    val boardDate = LocalDate.parse(board.date)
                    boardDate.isEqual(selectedStartDate) // 선택된 날짜와 동일한 게시글만 필터링
                }
            }
        }
    }

    // 게시글 상세 조회
    fun getPostDetail(dailyId: Int) {
        viewModelScope.launch {
            val response = repository.getPostDetail(dailyId)
            if (response.isSuccessful) {
                _postDetail.value = response.body()
            } else {
                // 실패 처리 로직
            }
        }
    }

    // 일기 작성
    fun createDiary(request: CreateDiaryRequest) {
        viewModelScope.launch {
            val response = repository.createDiary(request)
            if (response.isSuccessful) {
                _diaryCreatedResponse.value = response.body()
                _diaryCreated.value = true
            } else {
                _diaryCreated.value = false
            }
        }
    }

    // 일기 수정
    fun updateDiary(dailyId: Int, request: UpdateDiaryRequest) {
        viewModelScope.launch {
            val response = repository.updateDiary(dailyId, request)
            _diaryUpdated.value = response.isSuccessful
        }
    }
    // 일기 삭제
    fun deleteDiary(dailyId: Int) {
        viewModelScope.launch {
            val response = repository.deleteDiary(dailyId)
            _diaryDeleted.value = response.isSuccessful
        }
    }

    // 댓글 등록
    fun addComment(dailyId: Int, request: CreateCommentRequest) {
        viewModelScope.launch {
            repository.addComment(dailyId, request)
        }
    }


    // 댓글 수정
    fun updateComment(dailyId: Int, commentId: Int, request: UpdateCommentRequest) {
        viewModelScope.launch {
            repository.updateComment(dailyId, commentId, request)
        }
    }

    // 댓글 삭제
    fun deleteComment(dailyId: Int, commentId: Int) {
        viewModelScope.launch {
            repository.deleteComment(dailyId, commentId)
        }
    }

    // 좋아요 토글
    fun toggleLike(dailyId: Int) {
        viewModelScope.launch {
            val response = repository.toggleDiaryLike(dailyId)
            _likeToggled.value = response.isSuccessful
        }
    }
}
