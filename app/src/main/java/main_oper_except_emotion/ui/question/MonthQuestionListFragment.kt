package main_oper_except_emotion.ui.question

import android.R
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.main_oper_except_emotion.databinding.FragmentMonthQuestionListBinding
import dagger.hilt.android.AndroidEntryPoint
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

        // 툴바 설정
        val toolbar = binding.toolbarEmotion
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // 어댑터 설정
        adapter = MonthlyQuestionAdapter { questionId ->
            val action = MonthQuestionListFragmentDirections
                .actionMonthQuestionListFragmentToMonthlyAnswerDetailFragment(questionId.toLong())
            findNavController().navigate(action)
        }
        binding.recyclerMonthlyQuestions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMonthlyQuestions.adapter = adapter

        // ✅ 스피너 드롭다운 설정
        val monthItems = listOf("2025년 5월", "2025년 4월", "2025년 3월")
        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, monthItems)
        binding.spinnerWeekSelector.setAdapter(spinnerAdapter)

        binding.spinnerWeekSelector.setOnItemClickListener { _, _, position, _ ->
            val selected = monthItems[position]
            val year = selected.substring(0, 4).toInt()
            val month = selected.substringAfter("년 ").substringBefore("월").trim().toInt()
            viewModel.loadMonthlyQna(year, month)
        }

        // 기본 로딩
        val year = LocalDate.now().year
        val month = LocalDate.now().monthValue
        viewModel.loadMonthlyQna(year, month)

        viewModel.monthlyQnas.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.tvEmptyMessage.visibility =
                if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

