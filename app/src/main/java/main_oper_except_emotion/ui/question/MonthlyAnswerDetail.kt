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
import com.example.main_oper_except_emotion.databinding.FragmentMonthlyAnswerDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.viewmodel.QuestionViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject


@AndroidEntryPoint
class MonthlyAnswerDetailFragment : Fragment() {

    private var _binding: FragmentMonthlyAnswerDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuestionViewModel by activityViewModels()

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

        binding.toolbarEmotion.setNavigationOnClickListener {
            findNavController().navigateUp()
        }


        val questionId = arguments?.getLong("questionId") ?: return
        viewModel.loadQuestionDetail(questionId) // 서버에서 진짜 질문 상세 요청


        viewModel.questionDetail.observe(viewLifecycleOwner) { detail ->
            binding.tvQuestionText.text = detail.dailyQuestion

            val startDate = tokenManager.getStartDate()
            val daysText = startDate?.let {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val start = LocalDate.parse(it, formatter)
                val today = LocalDate.now()
                val diff = ChronoUnit.DAYS.between(start, today).toInt() + 1
                " · 우리 가족 ${diff}일 차"
            } ?: ""

            binding.tvQuestionMeta.text = "#${detail.questionId}번째 문답  ${detail.date}${daysText}"

            val myId = tokenManager.getUserId()?.toIntOrNull()
            val myAnswer = detail.answers.find { it.user_id.toInt() == myId }
            binding.tvMyAnswer.text = myAnswer?.answer ?: "아직 작성하지 않았어요"

            val nameViews = listOf(
                binding.tvOtherName1,
                binding.tvOtherName2,
                binding.tvOtherName3,
                binding.tvOtherName4,
                binding.tvOtherName5,
                binding.tvOtherName6,
                binding.tvOtherName7,
                binding.tvOtherName8,
                binding.tvOtherName9,
                binding.tvOtherName10
            )
            val answerViews = listOf(
                binding.tvOtherAnswer1,
                binding.tvOtherAnswer2,
                binding.tvOtherAnswer3,
                binding.tvOtherAnswer4,
                binding.tvOtherAnswer5,
                binding.tvOtherAnswer6,
                binding.tvOtherAnswer7,
                binding.tvOtherAnswer8,
                binding.tvOtherAnswer9,
                binding.tvOtherAnswer10
            )

            val otherAnswers = detail.answers.filter { it.user_id.toInt() != myId }

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
    }


        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
