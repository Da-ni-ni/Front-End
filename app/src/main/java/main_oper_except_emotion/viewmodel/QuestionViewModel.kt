package main_oper_except_emotion.viewmodel

import android.accounts.NetworkErrorException
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import main_oper_except_emotion.Answer
import main_oper_except_emotion.repsoitory.QuestionRepository
import main_oper_except_emotion.requestandresponse.question.DailyQuestionResponse
import main_oper_except_emotion.requestandresponse.question.MonthlyQuestion
import main_oper_except_emotion.requestandresponse.question.QuestionDetailResponse
import main_oper_except_emotion.requestandresponse.question.SubmitAnswerRequest
import main_oper_except_emotion.requestandresponse.question.UpdateAnswerRequest
import java.io.IOException
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val repository: QuestionRepository
) : ViewModel() {

    // LiveData 정의
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
    fun loadTodayQuestion() {
        viewModelScope.launch {
            val result = repository.getDailyQuestion()
            result.onSuccess {
                _todayQuestion.value = it
            }
                .onFailure { exception ->
                    val errorMsg = when (exception) {
                        is NetworkErrorException -> "네트워크 오류: 인터넷 연결을 확인하세요."
                        else -> "질문 조회 실패: ${exception.localizedMessage}"
                    }
                    _errorMessage.value = errorMsg
                }
        }
    }

//     2. 답변 제출
fun submitAnswer(questionId: Int, request: SubmitAnswerRequest) {
    val currentDetail = _questionDetail.value
    if (currentDetail != null) {
        val updatedAnswers = currentDetail.answers.map {
            if (it.question_id == questionId) {
                it.copy(answer = request.answer)  // 수정된 답변 반영
            } else {
                it
            }
        }
        val updatedDetail = currentDetail.copy(answers = updatedAnswers)
        _questionDetail.value = updatedDetail  // LiveData 업데이트
    }
}


//    // 답변 제출 (서버와 상관없이 하드코딩된 데이터로 처리)
//    fun submitAnswer(questionId: Int, request: SubmitAnswerRequest) {
//        val currentDetail = _questionDetail.value
//        if (currentDetail != null) {
//            val updatedAnswers = currentDetail.answers.map {
//                if (it.user_id == 101) it.copy(answer = request.answer) else it  // 하드코딩된 사용자 ID
//            }
//            val updatedDetail = currentDetail.copy(answers = updatedAnswers)
//            _questionDetail.value = updatedDetail  // 로컬 데이터 업데이트
//        }
//    }


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
    fun updateAnswer(questionId: Int, newAnswer: String?) {
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

//     5. 답변 삭제
fun deleteAnswer(questionId: Int) {
    val currentDetail = _questionDetail.value
    if (currentDetail != null) {
        val updatedAnswers = currentDetail.answers.map {
            if (it.question_id == questionId && it.user_id == 101) {
                it.copy(answer = null)  // 답변 삭제 처리
            } else {
                it
            }
        }
        val updatedDetail = currentDetail.copy(answers = updatedAnswers)
        _questionDetail.value = updatedDetail  // LiveData 업데이트
    }
}


//    // 답변 삭제 (서버와 상관없이 하드코딩된 데이터로 처리)
//    fun deleteAnswer(questionId: Int) {
//        val currentDetail = _questionDetail.value
//        if (currentDetail != null) {
//            val updatedAnswers = currentDetail.answers.map {
//                if (it.user_id == 101) it.copy(answer = null) else it  // 하드코딩된 사용자 ID
//            }
//            val updatedDetail = currentDetail.copy(answers = updatedAnswers)
//            _questionDetail.value = updatedDetail  // 로컬 데이터 업데이트
//        }
//        _answerDeleted.value = true
//    }
//     6. 질문 상세 조회
    fun loadQuestionDetail(questionId: Int) {
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
//    fun loadQuestionDetail(questionId: Int) {
//        viewModelScope.launch {
//            val result = repository.getQuestionDetail(questionId)
//            result.onSuccess {
//                _questionDetail.value = it
//            }.onFailure { exception ->
//                // 에러 처리
//            }
//        }
//    }
    // 7. 로컬 답변 업데이트
    fun updateLocalMyAnswer(answer: String) {
        _localMyAnswer.value = answer
    }

    // 8. 하드코딩된 질문 상세 보기 설정 (예시)
    fun setFakeQuestionDetailById(questionId: Int) {
        val detail = when (questionId) {
            1 -> QuestionDetailResponse(
                question_id = 1,
                date = "2025-05-01",
                daily_question = "오늘 가장 기억에 남는 일은?",
                answers = listOf(
                    Answer(user_id = 100, nickname = "엄마", answer = "햇살 좋은 아침",question_id = 1),
                    Answer(user_id = 101, nickname = "아빠", answer = "커피 한잔의 여유",question_id = 1)
                )
            )
            2 -> QuestionDetailResponse(
                question_id = 2,
                date = "2025-05-02",
                daily_question = "가장 좋아하는 음식은?",
                answers = listOf(
                    Answer(user_id = 100, nickname = "엄마", answer = "김치찌개",question_id=2),
                    Answer(user_id = 101, nickname = "아빠", answer = "비빔밥",question_id=2)
                )
            )
            3 -> QuestionDetailResponse(
                question_id = 3,
                date = "2025-05-03",
                daily_question = "가장 소중한 추억은 무엇인가요?",
                answers = listOf(
                    Answer(user_id = 101, nickname = "나", answer = "I am fine", question_id = 3),
                    Answer(user_id = 101, nickname = "엄마", answer = "아이를 처음 안은 날", question_id = 3),
                    Answer(user_id = 102, nickname = "아빠", answer = "결혼식", question_id = 3)
                )
            )
            else -> QuestionDetailResponse(
                question_id = questionId,
                date = "알 수 없음",
                daily_question = "질문 없음",
                answers = emptyList()
            )
        }
        _questionDetail.value = detail
    }

    // 9. 하드코딩된 답변 제출 (테스트용)
    // 답변 수정 (하드코딩된 데이터)
    fun fakesubmitAnswer(request: SubmitAnswerRequest) {
        val current = _questionDetail.value
        val myId = 101 // 사용자 ID (예시로 하드코딩)

        if (current != null) {
            // 현재 답변 중 해당 사용자 아이디에 맞는 답변을 수정
            val updatedAnswers = current.answers.map {
                if (it.user_id == myId) it.copy(answer = request.answer) else it
            }

            // 수정된 데이터를 LiveData에 반영
            val updatedDetail = current.copy(answers = updatedAnswers)
            _questionDetail.value = updatedDetail
        }
    }

    // 답변 삭제 (하드코딩된 데이터)
    fun fakedeleteAnswer() {
        val current = _questionDetail.value
        val myId = 101 // 사용자 ID (예시로 하드코딩)

        if (current != null) {
            // 해당 사용자의 답변을 null로 처리하여 삭제
            val updatedAnswers = current.answers.map {
                if (it.user_id == myId) it.copy(answer = null) else it
            }

            // 삭제된 데이터를 LiveData에 반영
            val updatedDetail = current.copy(answers = updatedAnswers)
            _questionDetail.value = updatedDetail
        }
    }



    fun updateQuestionDetail(questionDetail: QuestionDetailResponse) {
        _questionDetail.value = questionDetail
    }

    // 새로운 질문 추가 (하드코딩된 데이터)
    fun addNewQuestion(question: QuestionDetailResponse) {
        // 예시: 새로운 질문을 추가
        val currentDetails = _questionDetail.value?.let { listOf(it) } ?: emptyList()
        val updatedDetails = currentDetails + question  // 새로운 질문 추가

        // 업데이트된 데이터를 LiveData에 반영
        _questionDetail.value = updatedDetails.first()
    }


}