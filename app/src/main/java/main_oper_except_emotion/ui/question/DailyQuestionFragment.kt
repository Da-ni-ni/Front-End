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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarEmotion.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_dailyQuestionFragment_to_monthQuestionListFragment)
        }

        binding.viewCardArea.setOnClickListener {
            findNavController().navigate(R.id.action_dailyQuestionFragment_to_answerWriteFragment)
        }


        // 시작 날짜 기준, 일차 표시
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


