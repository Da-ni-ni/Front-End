package main_oper_except_emotion.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.main_oper_except_emotion.R
import com.example.main_oper_except_emotion.databinding.FragmentJoinBinding
import dagger.hilt.android.AndroidEntryPoint
import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.viewmodel.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class JoinFragment : Fragment() {

    private val authViewModel: AuthViewModel by viewModels()
    private var _binding: FragmentJoinBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뒤로 가기
        val toolbar: Toolbar = binding.toolbarJoin
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // ✅ 중복 확인 버튼 클릭 시
        binding.btCheckEmail.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()

            if (email.isBlank()) {
                Toast.makeText(requireContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(requireContext(), "올바른 이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.checkEmailDuplication(email)


        }

        // ✅ 회원가입 버튼 클릭 시
        binding.btJoginNext.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.signup(name, email, password)
        }

        // ✅ 이메일 중복 확인 결과 관찰
        authViewModel.isEmailDuplicated.observe(viewLifecycleOwner) { duplicated ->
            duplicated?.let {
                if (it) {
                    Toast.makeText(requireContext(), "이미 사용 중인 이메일입니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "사용 가능한 이메일입니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // ✅ 회원가입 성공 여부 관찰
        authViewModel.signupResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "회원가입 성공! 로그인해주세요.", Toast.LENGTH_SHORT).show()
                findNavController().navigate(JoinFragmentDirections.actionJoinFragmentToLoginFragment())
            } else {
                Toast.makeText(requireContext(), "회원가입 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
