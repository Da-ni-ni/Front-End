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
import com.example.main_oper_except_emotion.databinding.FragmentDailyAnswerBinding
import dagger.hilt.android.AndroidEntryPoint
import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.viewmodel.QuestionViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject


@AndroidEntryPoint
class DailyAnswerFragment : Fragment() {

    private var _binding: FragmentDailyAnswerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuestionViewModel by activityViewModels()

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyAnswerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔹 툴바 뒤로가기
        binding.toolbarEmotion.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // ✅ 1. localMyAnswer → 내 답변을 즉시 반영
        viewModel.localMyAnswer.observe(viewLifecycleOwner) { tempAnswer ->
            if (!tempAnswer.isNullOrBlank()) {
                binding.tvMyAnswer.text = tempAnswer
            }
        }

        // 🔹 가족 답변 뷰 초기화
        val nameViews = listOf(
            binding.tvOtherName1, binding.tvOtherName2, binding.tvOtherName3, binding.tvOtherName4,
            binding.tvOtherName5, binding.tvOtherName6, binding.tvOtherName7, binding.tvOtherName8,
            binding.tvOtherName9, binding.tvOtherName10
        )
        val answerViews = listOf(
            binding.tvOtherAnswer1, binding.tvOtherAnswer2, binding.tvOtherAnswer3, binding.tvOtherAnswer4,
            binding.tvOtherAnswer5, binding.tvOtherAnswer6, binding.tvOtherAnswer7, binding.tvOtherAnswer8,
            binding.tvOtherAnswer9, binding.tvOtherAnswer10
        )
        for (i in nameViews.indices) {
            nameViews[i].visibility = View.GONE
            answerViews[i].visibility = View.GONE
        }

        // 🔹 오늘의 질문 + 상세 답변 로드
        viewModel.loadTodayQuestion()
        viewModel.todayQuestion.observe(viewLifecycleOwner) { question ->
            question.questionId?.let { viewModel.loadQuestionDetail(it) }

        }

        // ✅ 2. 서버 응답 시 내 답변 + 가족 답변 표시
        viewModel.questionDetail.observe(viewLifecycleOwner) { detail ->
            binding.tvQuestionText.text = detail.dailyQuestion

            // 가족 N일차 계산
            val startDate = tokenManager.getStartDate()
            val daysText = startDate?.let {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val start = LocalDate.parse(it, formatter)
                val today = LocalDate.now()
                val diff = ChronoUnit.DAYS.between(start, today).toInt() + 1
                " · 우리 가족 ${diff}일 차"
            } ?: ""

            binding.tvQuestionMeta.text = "#${detail.questionId}번째 문답  ${detail.date}${daysText}"

            // 내 답변 서버 기준으로 덮어쓰기
            val myId = tokenManager.getUserId()?.toLongOrNull()
            val answers = detail.answers
            val myAnswer = answers.find { it.user_id.toLong() == myId }

            if (myAnswer?.answer.isNullOrBlank()) {
                viewModel.localMyAnswer.value?.let {
                    binding.tvMyAnswer.text = it
                }
            } else {
                if (myAnswer != null) {
                    binding.tvMyAnswer.text = myAnswer.answer
                }
            }

            val otherAnswers = answers.filter { it.user_id.toLong() != myId }
            for (i in otherAnswers.indices) {
                if (i < nameViews.size) {
                    nameViews[i].text = otherAnswers[i].nickname
                    answerViews[i].text = otherAnswers[i].answer ?: "아직 작성하지 않았어요"
                    nameViews[i].visibility = View.VISIBLE
                    answerViews[i].visibility = View.VISIBLE
                }
            }

            for (i in otherAnswers.size until nameViews.size) {
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
