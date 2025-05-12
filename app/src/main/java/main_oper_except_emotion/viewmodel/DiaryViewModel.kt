package main_oper_except_emotion.viewmodel

import android.os.Build
import android.util.Log
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
import main_oper_except_emotion.requestandresponse.diary.Comment
import main_oper_except_emotion.requestandresponse.diary.CreateDiaryRequest
import main_oper_except_emotion.requestandresponse.diary.UpdateDiaryRequest
import main_oper_except_emotion.requestandresponse.diary.CreateCommentRequest
import main_oper_except_emotion.requestandresponse.diary.CreateDiaryResponse
import main_oper_except_emotion.requestandresponse.diary.Post
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

    // 주간 리스트
    private val _weeklyBoards = MutableStateFlow<List<WeekBoardCheckResponse>>(emptyList())
    val weeklyBoards: StateFlow<List<WeekBoardCheckResponse>> = _weeklyBoards

    // 상세 조회
    private val _postDetail = MutableStateFlow<PostDetailResponse?>(null)
    val postDetail: StateFlow<PostDetailResponse?> = _postDetail

    // 일기 작성 성공 여부
    private val _diaryCreated = MutableLiveData<Boolean>()
    val diaryCreated: LiveData<Boolean> get() = _diaryCreated

    // daily_id, 작성 시점
    private val _diaryCreatedResponse = MutableLiveData<CreateDiaryResponse>()
    val diaryCreatedResponse: LiveData<CreateDiaryResponse> get() = _diaryCreatedResponse

    // 업데이트 여부
    private val _diaryUpdated = MutableLiveData<Boolean>()
    val diaryUpdated: LiveData<Boolean> get() = _diaryUpdated

    // 삭제 여부
    private val _diaryDeleted = MutableStateFlow<Boolean?>(null)
    val diaryDeleted: StateFlow<Boolean?> = _diaryDeleted

    // 좋아요 여부
    private val _likeToggled = MutableLiveData<Boolean>()
    val likeToggled: LiveData<Boolean> get() = _likeToggled

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _weeklyDiaries = MutableStateFlow<List<WeekBoardCheckResponse>>(emptyList())
    val weeklyDiaries: StateFlow<List<WeekBoardCheckResponse>> = _weeklyDiaries

    // 주간 게시글 가져오기
    // 실제용 코드
    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchWeeklyDiaries(selectedStartDate: LocalDate) {
        viewModelScope.launch {
            val localData = listOf(
                WeekBoardCheckResponse(1, "2025-05-05", "너무 졸립다", 4, 3, "인태", "12:30")
            )

            // 주간 범위 계산 (선택된 시작일과 6일 뒤까지)
            val weekEndDate = selectedStartDate.plusDays(6)
            Log.d("DiaryViewModel", "Selected week range: $selectedStartDate to $weekEndDate")

            // filter 시작 전에 로그 찍기
            Log.d(
                "DiaryViewModel",
                "Starting filter process with localData size: ${localData.size}"
            )

            // filter 실행 여부 확인
            val filteredData = localData.filter { board ->
                // 필터가 실행되는지 확인하기 위해 로그 찍기
                Log.d("DiaryViewModel", "Filtering board with date: ${board.date}")

                try {
                    // board.date를 LocalDate로 변환
                    val boardDate = LocalDate.parse(board.date)

                    // 비교하는 날짜들 출력
                    Log.d(
                        "DiaryViewModel",
                        "Comparing board date: $boardDate with selectedStartDate: $selectedStartDate and weekEndDate: $weekEndDate"
                    )

                    // boardDate가 selectedStartDate와 weekEndDate 범위 내에 있는지 확인
                    boardDate.isAfter(selectedStartDate.minusDays(1)) && boardDate.isBefore(
                        weekEndDate.plusDays(1)
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }

            if (filteredData.isEmpty()) {
                Log.d("DiaryViewModel", "No boards available")
            } else {
                Log.d("DiaryViewModel", "Filtered Data Size: ${filteredData.size}")
            }

            _weeklyBoards.value = filteredData // 필터링된 데이터를 _weeklyBoards에 할당
        }
    }


    // 게시글 필터링
//                _weeklyBoards.value = fullList.filter { board ->
//                    val boardDate = LocalDate.parse(board.date)
//                    boardDate.isEqual(selectedStartDate) // 선택된 날짜와 동일한 게시글만 필터링
//                }
//          }
//        }
//    }


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