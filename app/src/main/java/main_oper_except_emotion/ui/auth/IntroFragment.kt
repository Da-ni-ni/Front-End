package main_oper_except_emotion.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.main_oper_except_emotion.R
import com.example.main_oper_except_emotion.databinding.FragmentIntroBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class
IntroFragment : Fragment() {
    private var _binding: FragmentIntroBinding? = null
    private val binding get() = _binding!! // null이면 안돼!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIntroBinding.inflate(inflater, container, false)
        binding.root
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btLogin.setOnClickListener {
            try {
                val action1 = IntroFragmentDirections.actionIntroFragmentToLoginFragment()
                findNavController().navigate(action1)//action을 지원해주는 함수, 실제로 화면의 이동을 제어해줌
            }catch (e : Exception){
                e.printStackTrace()
            }
        }

        binding.btJoin.setOnClickListener {
            val action2 = IntroFragmentDirections.actionIntroFragmentToJoinFragment()
            findNavController().navigate(action2)//action을 지원해주는 함수, 실제로 화면의 이동을 제어해줌
        }
    }
}