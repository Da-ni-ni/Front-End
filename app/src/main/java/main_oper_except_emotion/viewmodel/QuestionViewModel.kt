package main_oper_except_emotion.viewmodel

import android.accounts.NetworkErrorException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import main_oper_except_emotion.repsoitory.QuestionRepository
import main_oper_except_emotion.requestandresponse.question.*
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val repository: QuestionRepository
) : ViewModel() {

    // 오늘의 질문의 라이브 데이터
    private val _todayQuestion = MutableLiveData<DailyQuestionResponse>()
    val todayQuestion: LiveData<DailyQuestionResponse> = _todayQuestion

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _answerSubmitted = MutableLiveData<Boolean>()
    val answerSubmitted: LiveData<Boolean> = _answerSubmitted

    private val _monthlyQnas = MutableLiveData<List<MonthlyQuestion>>()
    val monthlyQnas: LiveData<List<MonthlyQuestion>> get() = _monthlyQnas

    private val _answerUpdated = MutableLiveData<Boolean>()
    val answerUpdated: LiveData<Boolean> = _answerUpdated

    private val _answerDeleted = MutableLiveData<Boolean>()
    val answerDeleted: LiveData<Boolean> = _answerDeleted

    private val _questionDetail = MutableLiveData<QuestionDetailResponse>()
    val questionDetail: LiveData<QuestionDetailResponse> = _questionDetail

    private val _localMyAnswer = MutableLiveData<String>()
    val localMyAnswer: LiveData<String> get() = _localMyAnswer


    // 1. 오늘의 질문 불러오기
    // 오늘의 질문 갖고 올 때, 질문 아이디도 갖고옴
    fun loadTodayQuestion() {
        viewModelScope.launch {
            val result = repository.getDailyQuestion()
            result.onSuccess {
                _todayQuestion.value = it
            }.onFailure { exception ->
                val errorMsg = when (exception) {
                    is NetworkErrorException -> "네트워크 오류: 인터넷 연결을 확인하세요."
                    else -> "질문 조회 실패: ${exception.localizedMessage}"
                }
                _errorMessage.value = errorMsg
            }
        }
    }

    // 2. 답변 제출
    fun submitAnswer(questionId: Long, request: SubmitAnswerRequest) {
        viewModelScope.launch {
            val result = repository.submitAnswer(questionId, request)
            result.onSuccess {
                _answerSubmitted.value = true
            }.onFailure { exception ->
                _answerSubmitted.value = false
                _errorMessage.value = "답변 제출 실패: ${exception.localizedMessage}"
            }
        }
    }

    // 3. 월간 문답 불러오기
    fun loadMonthlyQna(year: Int, month: Int) {
        viewModelScope.launch {
            val result = repository.getMonthlyQna(year, month)
            result.onSuccess {
                _monthlyQnas.value = it
            }.onFailure { exception ->
                val errorMsg = when (exception) {
                    is IOException -> "네트워크 오류: 인터넷 연결을 확인하세요."
                    else -> "월간 문답 조회 실패: ${exception.localizedMessage}"
                }
                _errorMessage.value = errorMsg
            }
        }
    }

    // 4. 답변 수정
    fun updateAnswer(questionId: Long, newAnswer: String?) {
        viewModelScope.launch {
            val request = UpdateAnswerRequest(answer = newAnswer ?: "답변이 없습니다")
            val result = repository.updateAnswer(questionId, request)

            result.onSuccess {
                _answerUpdated.value = true
            }.onFailure { exception ->
                _answerUpdated.value = false
                val errorMsg = when (exception) {
                    is IOException -> "네트워크 오류: 인터넷 연결을 확인하세요."
                    else -> "답변 수정 실패: ${exception.localizedMessage}"
                }
                _errorMessage.value = errorMsg
            }
        }
    }

    // 5. 답변 삭제
    fun deleteAnswer(questionId: Long) {
        viewModelScope.launch {
            val result = repository.deleteAnswer(questionId)
            result.onSuccess {
                _answerDeleted.value = true
            }.onFailure { exception ->
                _answerDeleted.value = false
                _errorMessage.value = "답변 삭제 실패: ${exception.localizedMessage}"
            }
        }
    }

    // 6. 질문 상세 조회
    fun loadQuestionDetail(questionId: Long) {
        viewModelScope.launch {
            val result = repository.getQuestionDetail(questionId)
            result.onSuccess {
                _questionDetail.value = it
            }.onFailure { exception ->
                val errorMsg = when (exception) {
                    is IOException -> "네트워크 오류: 인터넷 연결을 확인하세요."
                    else -> "상세 질문 불러오기 실패: ${exception.localizedMessage}"
                }
                _errorMessage.value = errorMsg
            }
        }
    }

    // 7. 로컬에서 내 답변만 임시 저장
    fun updateLocalMyAnswer(answer: String) {
        _localMyAnswer.value = answer
    }

    // 8. 외부에서 강제로 questionDetail 업데이트 (예: 테스트 시)
    fun updateQuestionDetail(questionDetail: QuestionDetailResponse) {
        _questionDetail.value = questionDetail
    }
}
