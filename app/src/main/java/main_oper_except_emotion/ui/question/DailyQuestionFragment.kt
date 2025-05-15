package main_oper_except_emotion.ui.question

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.main_oper_except_emotion.R
import com.example.main_oper_except_emotion.databinding.FragmentDailyQuestionBinding
import dagger.hilt.android.AndroidEntryPoint
import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.requestandresponse.question.SubmitAnswerRequest
import main_oper_except_emotion.viewmodel.QuestionViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@AndroidEntryPoint
class DailyQuestionFragment : Fragment() {

    private var _binding: FragmentDailyQuestionBinding? = null
    private val binding get() = _binding!!


    @Inject
    lateinit var tokenManager: TokenManager
    private val viewModel: QuestionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.toolbarEmotion.setNavigationOnClickListener {
            findNavController().navigate(
                R.id.action_dailyQuestionFragment_to_monthQuestionListFragment
            )
        }
        // ...

        // 질문 로딩
        viewModel.loadTodayQuestion()

        // 질문이 로딩되면 텍스트 표시
        viewModel.todayQuestion.observe(viewLifecycleOwner) { response ->
            binding.tvDailyQuestion.text = "${response.question}"
        }

        // [답변 제출 버튼 클릭 이벤트]
        binding.btNext.setOnClickListener {
            val answerText = binding.etMyAnswer.text.toString().trim()
            val questionId = viewModel.todayQuestion.value?.questionId

            if (questionId == null || answerText.isBlank()) {
                Toast.makeText(requireContext(), "답변을 입력해주세요!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🚫 중복 제출 방지
            binding.btNext.isEnabled = false

            val request = SubmitAnswerRequest(answer = answerText)
            viewModel.submitAnswer(questionId, request)
        }

        // [제출 성공 시 → 상세 페이지로 이동]
        viewModel.answerSubmitted.observe(viewLifecycleOwner) { success ->
            // ✅ 버튼 다시 활성화
            binding.btNext.isEnabled = true

            if (success) {
                val questionId = viewModel.todayQuestion.value?.questionId ?: return@observe
                val action = DailyQuestionFragmentDirections
                    .actionDailyQuestionFragmentToMonthlyAnswerDetailFragment(questionId)
                findNavController().navigate(action)
            } else {
                Toast.makeText(requireContext(), "답변 제출에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }



        // 시작 날짜 기준, 일차 표시
        // 무조건 시작 날짜를 앱 시작시 저장해야 돼
        // 친밀도 끝나고부터 저장하면 될 듯
        val startDate = tokenManager.getStartDate()
        val daysText = startDate?.let {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val start = LocalDate.parse(it, formatter)
            val today = LocalDate.now()
            val diff = ChronoUnit.DAYS.between(start, today).toInt() + 1
            "우리 가족 ${diff}일 차"
        } ?: "우리 가족 🤍"

        binding.tvAppStartDate.text = daysText

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



