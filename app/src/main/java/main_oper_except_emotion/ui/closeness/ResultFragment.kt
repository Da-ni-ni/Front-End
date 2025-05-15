package main_oper_except_emotion.ui.closeness

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.main_oper_except_emotion.R
import com.example.main_oper_except_emotion.databinding.FragmentResultBinding
import dagger.hilt.android.AndroidEntryPoint
import main_oper_except_emotion.TokenManager
import javax.inject.Inject


@AndroidEntryPoint
class ResultFragment : Fragment(){
    @Inject
    lateinit var tokenManager : TokenManager

    private var _binding : FragmentResultBinding?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val personalScore = tokenManager.getPersonalScore()
        val groupScore = tokenManager.getGroupScore()


        binding.tvScore.text = if (personalScore > 0) "${groupScore}점 / ${personalScore}점"  else "--점 / --점"

        binding.btnHomeNext.setOnClickListener {
            findNavController().navigate(R.id.action_resultFragment_to_homeFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}