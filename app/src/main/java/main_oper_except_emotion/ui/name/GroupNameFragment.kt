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
import androidx.navigation.fragment.findNavController
import com.example.main_oper_except_emotion.R
import com.example.main_oper_except_emotion.databinding.FragmentGroupNameBinding
import dagger.hilt.android.AndroidEntryPoint
import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.viewmodel.EmotionViewModel
import javax.inject.Inject

@AndroidEntryPoint
class GroupNameFragment : Fragment() {
    private var _binding: FragmentGroupNameBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var tokenManager: TokenManager
    private val viewModel: EmotionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 확인 버튼 클릭
        binding.btNext.setOnClickListener {
            val new_name = binding.etNewNickname.text.toString().trim()

            if (new_name.isBlank()) {
                Toast.makeText(requireContext(), "가족명을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else if (new_name.length > 8) {
                Toast.makeText(requireContext(), "가족명은 8자 이내여야 합니다", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.updateFamilyGroupName(new_name)
            }
        }

        // 수정 성공 시 뒤로가기
        viewModel.familyNameUpdateSuccess.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                Toast.makeText(requireContext(), "가족명이 변경되었습니다", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

        // 툴바 뒤로가기
        binding.toolbarEmotion.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
