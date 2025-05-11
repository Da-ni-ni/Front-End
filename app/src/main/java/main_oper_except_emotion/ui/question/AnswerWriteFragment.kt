package main_oper_except_emotion.ui.question

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
import com.example.main_oper_except_emotion.R
import com.example.main_oper_except_emotion.databinding.FragmentAnswerWriteBinding
import com.example.main_oper_except_emotion.databinding.FragmentDailyQuestionBinding
import dagger.hilt.android.AndroidEntryPoint
import main_oper_except_emotion.Answer
import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.requestandresponse.question.QuestionDetailResponse
import main_oper_except_emotion.requestandresponse.question.SubmitAnswerRequest
import main_oper_except_emotion.ui.diary.WeekBoardFragmentDirections
import main_oper_except_emotion.viewmodel.QuestionViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject


@AndroidEntryPoint
class AnswerWriteFragment : Fragment() {

    private var _binding: FragmentAnswerWriteBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var tokenManager: TokenManager
    private val viewModel: QuestionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnswerWriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 툴바 뒤로가기




        viewModel.loadTodayQuestion()
        viewModel.todayQuestion.observe(viewLifecycleOwner) { question ->
            binding.tvDailyQuestion.text = question.question
            val startDate = tokenManager.getStartDate()
            val daysText = startDate?.let {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val start = LocalDate.parse(it, formatter)
                val today = LocalDate.now()
                val n = ChronoUnit.DAYS.between(start, today).toInt() + 1
                val dateText = today.format(DateTimeFormatter.ofPattern("M월 d일"))
                binding.tvCountAnswer.text = "${n}번째 질문  $dateText"
            }
        }

        binding.btnSubmit.setOnClickListener {
            val answerText = binding.etMyanswer.text.toString().trim()
            if (answerText.isEmpty()) {
                Toast.makeText(requireContext(), "답변을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val questionId = viewModel.todayQuestion.value?.question_id ?: 0
            // 답변 제출 (서버 연동 없이 로컬 데이터로 처리)
            viewModel.submitAnswer(questionId, SubmitAnswerRequest(answerText))
            viewModel.updateLocalMyAnswer(answerText)
            // 테스트용
            viewModel.fakesubmitAnswer(SubmitAnswerRequest(answerText))

            // 상세 페이지로 이동
            val action = AnswerWriteFragmentDirections.actionAnswerWriteFragmentToDailyQuestionFragment(questionId)
            findNavController().navigate(action)
        }

        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("답변을 삭제할까요?")
                .setPositiveButton("삭제") { _, _ ->
                    val questionId = viewModel.todayQuestion.value?.question_id ?: 0
                    viewModel.deleteAnswer(questionId)
                    viewModel.fakedeleteAnswer()// 로컬 데이터에서 답변 삭제
                }
                .setNegativeButton("취소", null)
                .show()
        }

        val newQuestion = QuestionDetailResponse(
            question_id = 42,
            date = "2025-05-04",
            daily_question = "새로운 질문은 무엇인가요?",
            answers = listOf(
                Answer(user_id = 101, nickname = "나", answer = "새로운 답변" , question_id = 4)
            )
        )

// 새로운 질문 추가
        viewModel.addNewQuestion(newQuestion)

        viewModel.answerDeleted.observe(viewLifecycleOwner) { deleted ->
            if (deleted) {
                binding.etMyanswer.setText("") // 답변 필드 초기화
                viewModel.updateLocalMyAnswer("") // 로컬 답변 초기화
                Toast.makeText(requireContext(), "답변이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
