package main_oper_except_emotion.ui.name

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import com.example.main_oper_except_emotion.databinding.FragmentPersonalfamilynameBinding
import dagger.hilt.android.AndroidEntryPoint
import main_oper_except_emotion.TokenManager
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

        val user_id = arguments?.getInt("user_id") ?: return
        val name = arguments?.getString("name") ?: return // getInt에서 getString으로 변경

        // ✅ 기존 이름 표시
    // 본래 이름 : 닉네임이 있으면 닉네임, 없으면 본명
        val originalName = tokenManager.getNickname(user_id) ?: name ?: "기본 이름"
        binding.tvOriginalName.text = originalName

        binding.btNext.setOnClickListener {
            val nickname = binding.etNewGroupName.text.toString().trim()
            if (nickname.isNotBlank()) {
                tokenManager.saveNickname(user_id, nickname)
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}