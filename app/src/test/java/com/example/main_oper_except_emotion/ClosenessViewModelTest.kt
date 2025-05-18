package com.example.main_oper_except_emotion

import com.example.appdanini.data.model.request.closeness.ClosenessScoreResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import main_oper_except_emotion.di.ClosenessQuestions
import main_oper_except_emotion.repsoitory.ClosenessRepository
import main_oper_except_emotion.util.TokenManager
import main_oper_except_emotion.viewmodel.ClosenessViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ClosenessViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ClosenessViewModel
    private lateinit var repository: ClosenessRepository
    private lateinit var tokenManager: TokenManager

    @Before
    fun setUp() {
        // 👉 여기!
        repository = mockk()
        tokenManager = mockk()

        every { tokenManager.getGroupId() } returns 1L
        justRun { tokenManager.saveScore(any(), any()) }

        viewModel = ClosenessViewModel(repository, tokenManager)
    }

    @Test
    fun `submitAnswer 마지막 질문까지 입력 시 점수 응답이 LiveData에 반영된다`() = runTest {
        coEvery { repository.submitAnswers(any()) } returns Response.success(Unit)
        coEvery { repository.getClosenessScores() } returns Response.success(
            ClosenessScoreResponse(groupScore = 88L, personalScore = 93L)
        )

        repeat(ClosenessQuestions.questions.size) {
            viewModel.submitAnswer(selectedOptionIndex = 2)
        }
        advanceUntilIdle()

        assertThat(viewModel.groupScore.getOrAwaitValue()).isEqualTo(88L)
        assertThat(viewModel.personalScore.getOrAwaitValue()).isEqualTo(93L)
        assertThat(viewModel.resultReady.getOrAwaitValue()).isTrue()
    }
}
