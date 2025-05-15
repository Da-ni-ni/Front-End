package main_oper_except_emotion.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.appdanini.data.model.request.closeness.Answer
import com.example.appdanini.data.model.request.closeness.ClosenessAnswerRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.di.ClosenessQuestions
import main_oper_except_emotion.repsoitory.ClosenessRepository
import javax.inject.Inject

@HiltViewModel
class ClosenessViewModel @Inject constructor(
    private val closenessRepository: ClosenessRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val groupId = tokenManager.getGroupId()

    private val _currentQuestionIndex = MutableLiveData(0)
    val currentQuestionIndex: LiveData<Int> = _currentQuestionIndex

    private val _currentQuestion = MutableLiveData<String>().apply {
        value = ClosenessQuestions.questions.firstOrNull() ?: "질문이 없습니다."
    }
    val currentQuestion: LiveData<String> = _currentQuestion

    private val answerList = mutableListOf<Answer>()

    private val _personalScore = MutableLiveData<Long?>()
    val personalScore: LiveData<Long?> = _personalScore

    private val _groupScore = MutableLiveData<Long?>()
    val groupScore: LiveData<Long?> = _groupScore

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _resultReady = MutableLiveData(false)
    val resultReady: LiveData<Boolean> get() = _resultReady

    // 선택지 선택 시 호출됨
    fun submitAnswer(selectedOptionIndex: Int) {
        val index = _currentQuestionIndex.value ?: 0
        val score = selectedOptionIndex + 1

        answerList.add(
            Answer(
                questionId = index + 1,
                groupId = groupId,
                answer = score
            )
        )

        if (index + 1 < ClosenessQuestions.questions.size) {
            _currentQuestionIndex.value = index + 1
            _currentQuestion.value = ClosenessQuestions.questions[index + 1]
        } else {
            submitAnswersAndFetchScores()
        }
    }

    // 서버로 답변 전송 후 점수 요청
    private fun submitAnswersAndFetchScores() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                // 1. 답변 먼저 전송
                val request = ClosenessAnswerRequest(answers = answerList)
                val postResponse = closenessRepository.submitAnswers(request)

                if (!postResponse.isSuccessful) {
                    Log.e("ClosenessViewModel", "답변 전송 실패: ${postResponse.code()}")
                    return@launch
                }

                // 2. 점수 요청
                val getResponse = closenessRepository.getClosenessScores()
                if (getResponse.isSuccessful) {
                    getResponse.body()?.let { result ->
                        tokenManager.saveScore(result.groupScore, result.personalScore)
                        _resultReady.value = true
                    }
                }
                else {
                    Log.e("ClosenessViewModel", "점수 조회 실패: ${getResponse.code()}")
                }
            } catch (e: Exception) {
                Log.e("ClosenessViewModel", "예외 발생: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
