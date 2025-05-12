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
import com.example.main_oper_except_emotion.databinding.FragmentWeekBoardBinding
import dagger.hilt.android.AndroidEntryPoint
import main_oper_except_emotion.requestandresponse.diary.WeekBoardCheckResponse
import main_oper_except_emotion.viewmodel.DiaryViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

// 주간 조회

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

        // 📌 주차 생성: 2025년 4월부터 시작해 현재와 미래 포함
        // 시작 날짜는 2025년 4월 1일.
        val startMonth = LocalDate.of(2025, 4, 1)

        // val monthsToGenerate = 12
        //총 12개월치 주차 데이터를 생성하겠다는 뜻 (
        val monthsToGenerate = 12

        //한국 기준으로 주차를 계산하겠다는 뜻.
        val weekFields = WeekFields.of(Locale.KOREA)

        // 25년 4월부터 12개월 동안의 주차 범위를 만들고, 이를 주차 라벨로 변환
        // flapMap은 입력 1개 -> 출력 여러 개
        // 2차원 리스트를 1차원 리스트로 만듦
        weekRanges = (0 until monthsToGenerate).flatMap { i ->
            val month = startMonth.plusMonths(i.toLong())
            // 한달씩 증가
            val firstDayOfMonth = month.withDayOfMonth(1)
            // 그 달의 첫 번째 날, 즉 1일을 얻는 거야.

            (1..5).mapNotNull { weekNumber -> // 1~5주차까지 반복
                // n번째 주의 월요일 날짜를 구하기 위함

                // 월요일, 일요일
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

        // weekRanges (주 시작일, 주 종료일)
        val weekLabels = weekRanges.map {
            val start = it.first
            "${start.year}년 ${start.monthValue}월 ${getWeekOfMonth(start)}주차"
        }

        // 어댑터(adapter)**는 데이터(=주차 문자열 리스트)를 Spinner에 연결해주는 도구
        val spinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, weekLabels)
        //드롭다운이 펼쳐졌을 때 어떻게 보일지 정하는 거야
        //이것도 안드로이드에서 제공하는 기본 스타일
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //이 줄로 주차 항목들이 화면에 보이도록 스피너에 연결되는 거야
        binding.spinnerWeekSelector.adapter = spinnerAdapter

        binding.spinnerWeekSelector.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedStartDate = weekRanges[position].first
                    Log.d(
                        "WeekBoardFragment",
                        "Spinner item selected, date: $selectedStartDate"
                    ) // 로그 확인
                    viewModel.fetchWeeklyDiaries(selectedStartDate)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }


        // 게시글 수신 후 리사이클 뷰에 표시
        lifecycleScope.launchWhenStarted {
            viewModel.weeklyBoards.collect { boards ->
                Log.d("WeekBoardFragment", "Boards received: ${boards.size} items") // 데이터 갱신 여부 확인
                adapter = WeeklyBoardAdapter(boards) { dailyId ->
                    val action =
                        WeekBoardFragmentDirections.actionWeekBoardFragmentToDetailPostFragment(
                            dailyId
                        )
                    findNavController().navigate(action)
                }
                binding.recyclerWeeklyBoard.adapter = adapter
            }



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

