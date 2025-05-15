package main_oper_except_emotion.ui.diary

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.main_oper_except_emotion.R
import com.example.main_oper_except_emotion.databinding.FragmentWriteBinding
import dagger.hilt.android.AndroidEntryPoint
import main_oper_except_emotion.requestandresponse.diary.CreateDiaryRequest
import main_oper_except_emotion.viewmodel.DiaryViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class WriteFragment : Fragment() {

    private var _binding: FragmentWriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DiaryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = binding.toolbarDiary
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // 답변 작성 버튼 클릭
        binding.btWriteBtn.setOnClickListener {
            val content = binding.etWrite.text.toString().trim()

            if (content.isEmpty()) {
                Toast.makeText(requireContext(), "내용을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm") // 또는 ISO 형식으로 변경 가능
            val formattedTime = currentTime.format(formatter)

            val requestCreateDiary = CreateDiaryRequest(content)
            viewModel.createDiary(requestCreateDiary)
        }

        // 작성 완료 시 이동
        viewModel.diaryCreatedResponse.observe(viewLifecycleOwner) { response ->
            response?.let {
                val dailyId = it.dailyId
                val action = WriteFragmentDirections.actionWriteFragmentToDetailPostFragment(dailyId)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
