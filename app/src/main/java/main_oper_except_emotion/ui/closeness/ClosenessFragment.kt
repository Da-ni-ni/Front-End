package main_oper_except_emotion.ui.closeness

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.main_oper_except_emotion.R
import com.example.main_oper_except_emotion.databinding.FragmentClosenessBinding
import dagger.hilt.android.AndroidEntryPoint
import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.viewmodel.ClosenessViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ClosenessFragment : Fragment() {

    @Inject
    lateinit var tokenManager: TokenManager
    private var _binding: FragmentClosenessBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ClosenessViewModel by viewModels()

    private val answerOptions = listOf(
        "매우 그렇다",  // 5점
        "조금 그렇다", // 4점
        "중간 정도다", // 3점
        "조금 아니다", // 2점
        "매우 아니다"  // 1점
    )

    private var selectedOptionIndex: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClosenessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.currentQuestion.observe(viewLifecycleOwner) { question ->
            animateQuestionChange(question)
        }

        viewModel.currentQuestionIndex.observe(viewLifecycleOwner) { index ->
            binding.tvQuestionNumber.text = "Q${index + 1}."
        }

        // ✅ 점수 수신 완료 시 초기 설정 저장 → 결과 화면으로 이동
        viewModel.resultReady.observe(viewLifecycleOwner) { ready ->
            if (ready == true) {
                tokenManager.markTestCompleted()
                tokenManager.markInitialSetupDone()
                findNavController().navigate(R.id.action_closenessFragment_to_resultFragment)
            }
        }

        listOf(
            binding.btnOption1,
            binding.btnOption2,
            binding.btnOption3,
            binding.btnOption4,
            binding.btnOption5
        ).forEachIndexed { index, button ->
            button.text = answerOptions[index]
            button.setOnClickListener {
                selectedOptionIndex = 4 - index
            }
        }

        binding.btnNext.setOnClickListener {
            if (selectedOptionIndex != null) {
                binding.btnNext.isEnabled = false
                viewModel.submitAnswer(selectedOptionIndex!!)
                selectedOptionIndex = null
                binding.btnNext.postDelayed({
                    binding.btnNext.isEnabled = true
                }, 500)
            } else {
                Toast.makeText(requireContext(), "선택지를 골라주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun animateQuestionChange(newQuestion: String) {
        val fadeOut = ObjectAnimator.ofFloat(binding.tvQuestion, "alpha", 1f, 0f)
        val fadeIn = ObjectAnimator.ofFloat(binding.tvQuestion, "alpha", 0f, 1f)

        fadeOut.duration = 200
        fadeIn.duration = 200

        fadeOut.doOnEnd {
            binding.tvQuestion.text = newQuestion
            fadeIn.start()
        }
        fadeOut.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}