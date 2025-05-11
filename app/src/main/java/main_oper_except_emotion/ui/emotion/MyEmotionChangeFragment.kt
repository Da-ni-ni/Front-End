package main_oper_except_emotion.ui.emotion

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.main_oper_except_emotion.R
import com.example.main_oper_except_emotion.databinding.FragmentMyEmotionChagneBinding
import dagger.hilt.android.AndroidEntryPoint
import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.requestandresponse.emotion.Emotion
import main_oper_except_emotion.viewmodel.EmotionViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MyEmotionChangeFragment : Fragment() {
    private var _binding: FragmentMyEmotionChagneBinding? = null
    private val binding get() = _binding!!
    @Inject lateinit var tokenManager: TokenManager
    private val viewModel: EmotionViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyEmotionChagneBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = binding.toolbarEmotion
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val savedName = tokenManager.getUserName()
        binding.textViewUserName.text = savedName ?: "나"

        // 감정 상태 복원
        viewModel.currentEmotion.observe(viewLifecycleOwner, { currentEmotion ->
            binding.currentEmotion.setImageResource(Emotion.getImageRes(currentEmotion))
        })

        setupEmotionListeners()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupEmotionListeners() {
        val emotionMap = mapOf(
            binding.emotionJoy to Emotion.JOY,
            binding.emotionSad to Emotion.SAD,
            binding.emotionTired to Emotion.TIRED,
            binding.emotionMissed to Emotion.MISSED,
            binding.emotionAngry to Emotion.ANGRY,
            binding.emotionComfort to Emotion.COMFORT
        )

        for ((view, emotion) in emotionMap) {
            view.setOnClickListener {
                // 감정 상태를 ViewModel에 저장
                viewModel.setMyEmotion(emotion) // 로컬 상태 반영
                viewModel.updateEmotion(emotion) // 서버에 감정 수정
            }
        }

        binding.currentEmotion.setOnClickListener{
            findNavController().navigate(R.id.action_myEmotionChangeFragment_to_myNameChangeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
