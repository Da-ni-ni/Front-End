package main_oper_except_emotion.ui.name

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import com.example.main_oper_except_emotion.databinding.FragmentPersonalfamilynameBinding
import dagger.hilt.android.AndroidEntryPoint
import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.requestandresponse.emotion.getImageRes
import main_oper_except_emotion.viewmodel.EmotionViewModel
import javax.inject.Inject

@AndroidEntryPoint
class PersonalFamilyNameFragment : Fragment() {

    private var _binding: FragmentPersonalfamilynameBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EmotionViewModel by activityViewModels()
    @Inject lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalfamilynameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = binding.toolbarEmotion
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val emotionId = arguments?.getLong("emotionId") ?: return
        val originalName = arguments?.getString("userName") ?: return //
        val nameExpress = "현재 가족 구성원의 닉네임은 ${originalName}입니다."

        // ✅ 기존 이름 표시
    // 본래 이름 : 닉네임이 있으면 닉네임, 없으면 본명

        viewModel.fetchPersonalEmotion(emotionId)

        // 감정 응답 옵저빙
        viewModel.personalEmotionDetail.observe(viewLifecycleOwner) { detail ->
            if (detail != null && detail.emotionId == emotionId) {
                // 이미지 리소스 설정
                val imageResId = getImageRes(detail.emotionType)  // 감정에 따라 이미지 리소스 가져오는 함수
                binding.ivDefaultEmotion.setImageResource(imageResId)

                // 이름도 표시 (닉네임 or 본명)
                binding.tvOriginalName.setText(nameExpress)

            }
        }

        binding.btNext.setOnClickListener {
            val nickname = binding.etNewName.text.toString().trim()
            if (nickname.isNotBlank()) {
                viewModel.updateOtherMemberName(emotionId,nickname)
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }else{
                Toast.makeText(requireContext(), "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}