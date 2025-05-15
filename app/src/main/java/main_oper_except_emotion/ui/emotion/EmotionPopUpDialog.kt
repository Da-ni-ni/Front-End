package main_oper_except_emotion.ui.emotion

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.main_oper_except_emotion.R
import com.example.main_oper_except_emotion.databinding.DialogEmotionPopupBinding
import dagger.hilt.android.AndroidEntryPoint
import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.requestandresponse.emotion.EmotionType
import main_oper_except_emotion.viewmodel.EmotionViewModel
import javax.inject.Inject


@AndroidEntryPoint
class EmotionPopupDialog : DialogFragment() {
    private var _binding: DialogEmotionPopupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EmotionViewModel by activityViewModels()

    @Inject lateinit var tokenManager: TokenManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DialogEmotionPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.resetSubmitStatus()
        setupEmotionListeners()
        observeSubmitResult()

        // ❌ 버튼 클릭 시 dismiss
        val btnClose = view.findViewById<ImageView>(R.id.btn_close)
        btnClose.setOnClickListener {
            dismiss()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupEmotionListeners() {
        val emotionMap = mapOf(
            binding.emotionJoy to EmotionType.HAPPY,
            binding.emotionSad to EmotionType.SAD,
            binding.emotionMissed to EmotionType.MISSING,
            binding.emotionTired to EmotionType.TIRED,
            binding.emotionAngry to EmotionType.ANGRY,
            binding.emotionComfort to EmotionType.RELAXED
        )

        emotionMap.forEach { (view, emotion) ->
            view.setOnClickListener {
                disableEmotionButtons()

                // 1. 로컬 상태에 반영하여 HomeFragment UI에 즉시 반영되도록 함
                viewModel.setMyEmotion(emotion)

                // 2. 서버에 감정 전송 (성공 여부와 무관하게 일단 진행)
                viewModel.submitEmotion(emotion)

            }
        }

        viewModel.submittedEmotionId.observe(viewLifecycleOwner) { emotionId ->
            Log.d("Emotion", "서버로부터 받은 감정 ID: $emotionId")
            tokenManager.saveEmotionId(emotionId)
        }



    }

    private fun observeSubmitResult() {
        viewModel.submitSuccess.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                Toast.makeText(requireContext(), "감정이 등록되었습니다", Toast.LENGTH_SHORT).show()
                dismiss()
            } else if (success == false) {
                Toast.makeText(requireContext(), "감정 등록 실패", Toast.LENGTH_SHORT).show()
                enableEmotionButtons()
            }

        }
    }

    private fun disableEmotionButtons() {
        listOf(
            binding.emotionJoy,
            binding.emotionSad,
            binding.emotionMissed,
            binding.emotionTired,
            binding.emotionAngry,
            binding.emotionComfort
        ).forEach { it.isEnabled = false }
    }

    private fun enableEmotionButtons() {
        listOf(
            binding.emotionJoy,
            binding.emotionSad,
            binding.emotionMissed,
            binding.emotionTired,
            binding.emotionAngry,
            binding.emotionComfort
        ).forEach { it.isEnabled = true }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT  // ✅ 전체 높이로 확장
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent) // 배경 투명하게
    }
}
