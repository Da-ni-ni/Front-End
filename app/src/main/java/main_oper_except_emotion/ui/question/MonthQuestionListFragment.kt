package main_oper_except_emotion.ui.question

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.main_oper_except_emotion.R
import com.example.main_oper_except_emotion.databinding.FragmentMonthQuestionListBinding
import dagger.hilt.android.AndroidEntryPoint
import main_oper_except_emotion.requestandresponse.question.MonthlyQuestion
import main_oper_except_emotion.viewmodel.QuestionViewModel
import java.time.LocalDate

@AndroidEntryPoint
class MonthQuestionListFragment : Fragment() {
    private var _binding: FragmentMonthQuestionListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuestionViewModel by activityViewModels()
    private lateinit var adapter: MonthlyQuestionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthQuestionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbarEmotion
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // ✅ 어댑터 설정 (클릭 시 네비게이션은 주석 처리)
        adapter = MonthlyQuestionAdapter { questionId ->
            val action = MonthQuestionListFragmentDirections
                .actionMonthQuestionListFragmentToMonthlyAnswerDetailFragment(questionId)
            findNavController().navigate(action)
        }

        binding.recyclerMonthlyQuestions.layoutManager = LinearLayoutManager(requireContext()) // ← 추가
        binding.recyclerMonthlyQuestions.adapter = adapter

        val fakeList = listOf(
            MonthlyQuestion(
                question_id = 42, // 이 ID가 중요!
                date = "2025-05-10",
                question = "가장 소중한 추억은 무엇인가요?"
            ),
            MonthlyQuestion(
                question_id = 1,
                date = "2025-05-01",
                question = "오늘 가장 기억에 남는 일은?"
            ),
            MonthlyQuestion(
                question_id = 2,
                date = "2025-05-02",
                question = "가장 좋아하는 음식은?"
            )
        )

        adapter.submitList(fakeList)

        binding.tvEmptyMessage.visibility = View.GONE

        val yearMonthList = mutableListOf<String>()
        for (year in 2024..2025) {
            for (month in 1..12) {
                yearMonthList.add("${year}년 ${month}월")
            }
        }

        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, yearMonthList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerYearMonth.adapter = spinnerAdapter

        binding.spinnerYearMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = yearMonthList[position]
                val parts = selected.replace("년", "").replace("월", "").split(" ")
                val year = parts[0].toInt()
                val month = parts[1].toInt()

                val filtered = fakeList.filter {
                    val (y, m, _) = it.date.split("-")
                    y.toInt() == year && m.toInt() == month
                }

                adapter.submitList(filtered)
                binding.tvEmptyMessage.visibility =
                    if (filtered.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.spinnerYearMonth.setSelection(yearMonthList.indexOf("2025년 5월"))


        // ✅ ViewModel + 서버 연결 로직은 주석 처리
//        val year = LocalDate.now().year
//        val month = LocalDate.now().monthValue
//        viewModel.loadMonthlyQna(year, month)
//
//        viewModel.monthlyQnas.observe(viewLifecycleOwner) { list ->
//            adapter.submitList(list)
//            binding.tvEmptyMessage.visibility =
//                if (list.isEmpty()) View.VISIBLE else View.GONE
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


