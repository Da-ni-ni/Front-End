package main_oper_except_emotion.ui.invite

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.main_oper_except_emotion.R
import com.example.main_oper_except_emotion.databinding.FragmentWaitInviteBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.viewmodel.AuthViewModel
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class WaitInviteFragment : Fragment() {

    private val authViewModel: AuthViewModel by viewModels()
    private var _binding: FragmentWaitInviteBinding? = null
    private val binding get() = _binding!!
    private var pollingJob: Job? = null
    private var hasNavigated = false

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWaitInviteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            // 아무것도 하지 않음 → 뒤로가기 막힘
        }


        startPollingInviteStatus()
    }

    private fun startPollingInviteStatus() {
        pollingJob = lifecycleScope.launch {
            while (isActive) {
                try {
                    val response = authViewModel.checkInviteStatus()
                    response?.let {
                        Log.d("InvitePolling", "응답: status=${it.status}, request_id=${it.requestId}")

                        when (it.status?.uppercase(Locale.ROOT)) {
                            "APPROVED" -> {
                                if (!hasNavigated) {
                                    hasNavigated = true

                                    // 초대 아이디를 여기서 저장함
                                    tokenManager.saveRequestCode(it.requestId.toString())
                                    navigateToSuccessPage()
                                    return@launch
                                } else {
                                    Log.d("InvitePolling", "이미 이동 완료되어 polling 중단")
                                    pollingJob?.cancel()
                                    return@launch
                                }
                            }

                            "REJECT" -> {
                                pollingJob?.cancel()
                                showRejectedDialog()
                                return@launch
                            }

                            "PENDING" -> {
                                // 대기 중 메시지나 상태 갱신 가능
                            }

                            else -> {
                                Log.w("InvitePolling", "Unknown status: ${it.status}")
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    context?.let {
                        Toast.makeText(
                            it,
                            "서버 연결이 불안정합니다. 다시 시도 중...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                delay(5000)
            }
        }
    }

    private fun navigateToSuccessPage() {
        val action = WaitInviteFragmentDirections.actionWaitInviteFragmentToClosenessIntroductionFragment()
        findNavController().navigate(action)
    }

    private fun showRejectedDialog() {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle("초대 거절")
                .setMessage("초대가 거절되었습니다.")
                .setPositiveButton("확인") { _, _ ->
                    findNavController().popBackStack()
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pollingJob?.cancel()
        _binding = null
    }
}

