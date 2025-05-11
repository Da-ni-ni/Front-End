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
import com.example.main_oper_except_emotion.databinding.FragmentMyNameChangeBinding
import dagger.hilt.android.AndroidEntryPoint
import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.viewmodel.EmotionViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MyNameChangeFragment : Fragment() {

    private var _binding: FragmentMyNameChangeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var tokenManager: TokenManager
    private val viewModel: EmotionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyNameChangeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbarEmotion
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.btNext.setOnClickListener {
            val newName = binding.etNewNickname.text.toString().trim()

            if (newName.isEmpty()) {
                Toast.makeText(requireContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newName.length > 10) {
                Toast.makeText(requireContext(), "10자 이하로 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.updateMyName(newName)

            Toast.makeText(requireContext(), "이름이 변경되었습니다", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
