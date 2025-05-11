package main_oper_except_emotion.ui.question

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.main_oper_except_emotion.databinding.FragmentMonthlyAnswerDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import main_oper_except_emotion.Answer
import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.requestandresponse.question.QuestionDetailResponse
import main_oper_except_emotion.viewmodel.QuestionViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MonthlyAnswerDetailFragment : Fragment() {

    private var _binding: FragmentMonthlyAnswerDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuestionViewModel by activityViewModels()
    val args: MonthlyAnswerDetailFragmentArgs by navArgs()

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthlyAnswerDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val questionId = args.questionId

        // 더미 데이터 생성
        val fakeAnswers = listOf(
            Answer(user_id = 1, nickname = "엄마", answer = "소중한 추억은 여행이에요.", question_id = 42),
            Answer(user_id = 2, nickname = "아빠", answer = "가족과 함께한 시간이 제일 소중해요.", question_id = 1)
        )

        // 질문에 맞는 데이터를 가져오고, 해당 questionId에 맞는 답변을 필터링
        val questionDetail = when (questionId) {
            42 -> QuestionDetailResponse(question_id = 42, date = "2025-05-10", daily_question = "가장 소중한 추억은 무엇인가요?", answers = fakeAnswers)
            1 -> QuestionDetailResponse(question_id = 1, date = "2025-05-01", daily_question = "오늘 가장 기억에 남는 일은?", answers = fakeAnswers)
            else -> QuestionDetailResponse(question_id = questionId, date = "알 수 없음", daily_question = "질문 없음", answers = emptyList())
        }

        binding.tvQuestionText.text = questionDetail.daily_question

        // 사용자 아이디
        val myId = tokenManager.getUserId()?.toIntOrNull()
        val myAnswer = questionDetail.answers.find { it.user_id == myId }
        binding.tvMyAnswer.text = myAnswer?.answer ?: "아직 작성하지 않았어요"

        val nameViews = listOf(
            binding.tvOtherName1,
            binding.tvOtherName2,
            binding.tvOtherName3,
            binding.tvOtherName4,
            binding.tvOtherName5
        )
        val answerViews = listOf(
            binding.tvOtherAnswer1,
            binding.tvOtherAnswer2,
            binding.tvOtherAnswer3,
            binding.tvOtherAnswer4,
            binding.tvOtherAnswer5
        )

        val otherAnswers = questionDetail.answers.filter { it.user_id != myId }

        for (i in nameViews.indices) {
            if (i < otherAnswers.size) {
                nameViews[i].text = otherAnswers[i].nickname
                answerViews[i].text = otherAnswers[i].answer ?: "아직 작성하지 않았어요"
                nameViews[i].visibility = View.VISIBLE
                answerViews[i].visibility = View.VISIBLE
            } else {
                nameViews[i].visibility = View.GONE
                answerViews[i].visibility = View.GONE
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
