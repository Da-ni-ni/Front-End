package main_oper_except_emotion.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.WeekBoardUiModel
import main_oper_except_emotion.repsoitory.DiaryRepository
import main_oper_except_emotion.requestandresponse.diary.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val repository: DiaryRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    // 해당 라이브 데이터 daily_id를, 상세 조회시 넘겨줘야함
    // 상세 조회할 때마다 daily_id를 받으니까, 저장x

    // 일기를 조회하기 위해서는, 주간 조회시 받은 데일리 아이디가 필요함.
    private val _postDetail = MutableLiveData<PostDetailResponse?>()
    val postDetail: LiveData<PostDetailResponse?> = _postDetail


    private val _diaryCreated = MutableLiveData<Boolean>()
    val diaryCreated: LiveData<Boolean> get() = _diaryCreated

    // 여기서 받는 daily_id로 뭘 할 생각을 하면 안될듯
    private val _diaryCreatedResponse = MutableLiveData<CreateDiaryResponse>()
    val diaryCreatedResponse: LiveData<CreateDiaryResponse> get() = _diaryCreatedResponse

    // 상세 조회하고 나서, 받는 라이브 데이터로 수정, 삭제를 하게 만들어야함.
    private val _diaryUpdated = MutableLiveData<Boolean>()
    val diaryUpdated: LiveData<Boolean> get() = _diaryUpdated

    private val _diaryDeleted = MutableLiveData<Boolean?>()
    val diaryDeleted: LiveData<Boolean?> = _diaryDeleted

    private val _likeToggled = MutableLiveData<Boolean>()
    val likeToggled: LiveData<Boolean> get() = _likeToggled

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _weeklyDiaries = MutableLiveData<List<WeekBoardCheckResponse>>(emptyList())
    val weeklyDiaries: LiveData<List<WeekBoardCheckResponse>> = _weeklyDiaries

    private val _selectedDailyId = MutableLiveData<Long?>()
    val selectedDailyId: LiveData<Long?> = _selectedDailyId

    private val _weeklyBoards = MutableLiveData<List<WeekBoardCheckResponse>>()
    val weeklyBoards: LiveData<List<WeekBoardCheckResponse>> = _weeklyBoards

    private val _weeklyUiBoards = MutableLiveData<List<WeekBoardUiModel>>()
    val weeklyUiBoards: LiveData<List<WeekBoardUiModel>> = _weeklyUiBoards

    private val _createdCommentId = MutableLiveData<Long?>()
    val createdCommentId: LiveData<Long?> get() = _createdCommentId


    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeeklyDiaries(selectedStartDate: LocalDate) {
        viewModelScope.launch {
            repository.getWeeklyDiaries()
                .onSuccess { list ->
                    _weeklyBoards.value = list // ✅ 원본 저장

                    val weekEndDate = selectedStartDate.plusDays(6)
                    val formatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREA)

                    val filtered = list.mapNotNull { board ->
                        try {
                            val dateTime = LocalDateTime.parse(board.date)
                            val boardDate = dateTime.toLocalDate()
                            if (boardDate.isBefore(selectedStartDate) || boardDate.isAfter(weekEndDate)) {
                                return@mapNotNull null
                            }

                            WeekBoardUiModel(
                                dailyId = board.dailyId,
                                date = boardDate.toString(),
                                time = dateTime.format(formatter),
                                authorName = board.authorName,
                                content = board.content,
                                likeCount = board.likeCount,
                                commentCount = board.commentCount
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }

                    _weeklyUiBoards.value = filtered // ✅ 가공본 저장
                }
                .onFailure {
                    _errorMessage.value = "주간 게시글 조회 실패: ${it.message}"
                }
        }

    }
    // 클릭시 아이디 저장
    fun selectDailyId(id: Long) {
        _selectedDailyId.value = id
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMyUserId(): Long? {
        return tokenManager.getUserIdFromToken()
    }

    fun getPostDetail(dailyId: Long) {
        viewModelScope.launch {

            repository.getPostDetail(dailyId)
                .onSuccess { _postDetail.value = it }
                .onFailure { _errorMessage.value = "게시글 상세 조회 실패: ${it.message}" }


        }
    }

    fun createDiary(request: CreateDiaryRequest) {
        viewModelScope.launch {
            repository.createDiary(request)
                .onSuccess {
                    _diaryCreatedResponse.value = it
                    _diaryCreated.value = true
                }
                .onFailure {
                    _diaryCreated.value = false
                    _errorMessage.value = "일기 작성 실패: ${it.message}"
                }
        }
    }

    fun updateDiary(dailyId: Long, request: UpdateDiaryRequest) {
        viewModelScope.launch {
            repository.updateDiary(dailyId, request)
                .onSuccess { _diaryUpdated.value = true }
                .onFailure {
                    _diaryUpdated.value = false
                    _errorMessage.value = "일기 수정 실패: ${it.message}"
                }
        }
    }

    fun deleteDiary(dailyId: Long) {
        viewModelScope.launch {
            repository.deleteDiary(dailyId)
                .onSuccess { _diaryDeleted.value = true }
                .onFailure {
                    _diaryDeleted.value = false
                    _errorMessage.value = "일기 삭제 실패: ${it.message}"
                }
        }
    }

    fun addComment(dailyId: Long, request: CreateCommentRequest) {
        viewModelScope.launch {
            repository.addComment(dailyId, request)
                .onSuccess {
                    _createdCommentId.value = it.commentId // ✅ 저장
                    getPostDetail(dailyId) // ✅ 최신 댓글 목록도 다시 조회
                }
                .onFailure {
                    _errorMessage.value = "댓글 등록 실패: ${it.message}"
                }
        }
    }

    fun updateComment(dailyId: Long, commentId: Long, request: UpdateCommentRequest) {
        viewModelScope.launch {
            repository.updateComment(dailyId, commentId, request)
                .onFailure {
                    _errorMessage.value = "댓글 수정 실패: ${it.message}"
                }
        }
    }

    fun deleteComment(dailyId: Long, commentId: Long) {
        viewModelScope.launch {
            repository.deleteComment(dailyId, commentId)
                .onFailure {
                    _errorMessage.value = "댓글 삭제 실패: ${it.message}"
                }
        }
    }

    fun toggleLike(dailyId: Long) {
        viewModelScope.launch {
            repository.toggleDiaryLike(dailyId)
                .onSuccess { _likeToggled.value = true }
                .onFailure {
                    _likeToggled.value = false
                    _errorMessage.value = "좋아요 토글 실패: ${it.message}"
                }
        }
    }
}