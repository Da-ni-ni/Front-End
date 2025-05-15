package main_oper_except_emotion.ui.diary

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.main_oper_except_emotion.R
import com.example.main_oper_except_emotion.databinding.FragmentWeekBoardBinding
import dagger.hilt.android.AndroidEntryPoint
import main_oper_except_emotion.requestandresponse.diary.WeekBoardCheckResponse
import main_oper_except_emotion.viewmodel.DiaryViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

// Fragment: WeekBoardFragment.kt
@AndroidEntryPoint
class WeekBoardFragment : Fragment() {

    private var _binding: FragmentWeekBoardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiaryViewModel by viewModels()
    private lateinit var adapter: WeeklyBoardAdapter
    private lateinit var weekRanges: List<Pair<LocalDate, LocalDate>>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeekBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerWeeklyBoard.layoutManager = LinearLayoutManager(requireContext())

        val startMonth = LocalDate.of(2025, 4, 1)
        val monthsToGenerate = 12
        val weekFields = WeekFields.of(Locale.KOREA)

        weekRanges = (0 until monthsToGenerate).flatMap { i ->
            val month = startMonth.plusMonths(i.toLong())
            val firstDayOfMonth = month.withDayOfMonth(1)

            (1..5).mapNotNull { weekNumber ->
                val weekStart = firstDayOfMonth
                    .with(weekFields.weekOfMonth(), weekNumber.toLong())
                    .with(DayOfWeek.MONDAY)

                if (weekStart.month == month.month) {
                    val weekEnd = weekStart.with(DayOfWeek.SUNDAY)
                    weekStart to weekEnd
                } else null
            }
        }.filter { it.first >= startMonth }
            .sortedByDescending { it.first }

        val weekLabels = weekRanges.map {
            val start = it.first
            "${start.year}년 ${start.monthValue}월 ${getWeekOfMonth(start)}주차"
        }

        val dropdownAdapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, weekLabels)
        binding.spinnerWeekSelector.setAdapter(dropdownAdapter)

        binding.spinnerWeekSelector.setOnItemClickListener { _, _, position, _ ->
            val selectedStartDate = weekRanges[position].first
            Log.d("WeekBoardFragment", "Spinner item selected, date: $selectedStartDate")
            viewModel.getWeeklyDiaries(selectedStartDate)
        }

        viewModel.weeklyUiBoards.observe(viewLifecycleOwner) { boards ->
            Log.d("WeekBoardFragment", "Boards received: ${boards.size} items")
            adapter = WeeklyBoardAdapter(boards) { item ->
                viewModel.selectDailyId(item.dailyId)
                val action = WeekBoardFragmentDirections.actionWeekBoardFragmentToDetailPostFragment(dailyId = item.dailyId)
                findNavController().navigate(action)
            }
            binding.recyclerWeeklyBoard.adapter = adapter
        }

        binding.fabWriteDiary.setOnClickListener {
            val action = WeekBoardFragmentDirections.actionWeekBoardFragmentToWriteFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeekOfMonth(date: LocalDate): Int {
        val weekFields = WeekFields.of(Locale.KOREA)
        return date.get(weekFields.weekOfMonth())
    }
}
