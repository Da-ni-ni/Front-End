package main_oper_except_emotion.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdanini.data.model.request.invite.GetJoinStatusResponse
import com.example.appdanini.data.model.request.invite.TransferInviteResponse
import com.example.appdanini.data.model.request.invite.UpdateFamilyNameRequest
import com.example.appdanini.data.model.request.invite.UpdateFamliyNameResponse
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.repsoitory.AuthRepository
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    // ✅ 로그인 결과
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult

    // ✅ 회원가입 결과
    private val _signupResult = MutableLiveData<Boolean>()
    val signupResult: LiveData<Boolean> = _signupResult

    // ✅ 이메일 중복 결과
    private val _isEmailDuplicated = MutableLiveData<Boolean?>()
    val isEmailDuplicated: LiveData<Boolean?> = _isEmailDuplicated

    // ✅ 강제 로그아웃 상태
    private val _shouldForceLogout = MutableLiveData<Boolean>()
    val shouldForceLogout: LiveData<Boolean> = _shouldForceLogout

    // (기존 초대코드 관련은 유지)
    private val _inviteCode = MutableLiveData<String?>()
    val inviteCode: LiveData<String?> = _inviteCode

    private val _transferInviteCodeResult = MutableLiveData<Boolean>()
    val transferInviteCodeResult: LiveData<Boolean> = _transferInviteCodeResult

    private val _inviteStatus = MutableLiveData<TransferInviteResponse?>()
    val inviteStatus: LiveData<TransferInviteResponse?> get() = _inviteStatus

    private val _joinRequestList = MutableLiveData<List<GetJoinStatusResponse>>() // ✅ 올바른 타입
    val joinRequestList: LiveData<List<GetJoinStatusResponse>> get() = _joinRequestList

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private var monitoringJob: Job? = null

    private val _loginError = MutableLiveData<String?>()
    val loginError: LiveData<String?> = _loginError

    private val _updateFamilyNameResponse = MutableLiveData<UpdateFamliyNameResponse?>()
    val updateFamilyNameResponse: LiveData<UpdateFamliyNameResponse?> = _updateFamilyNameResponse

    // ✅ 로그인
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = authRepository.loginAndGetResponse(email, password)

                if (response != null) {
                    // 얘는 라이브 데이터
                    _loginResult.value = true

                    // ✅ 토큰 저장은 Repository에서 했다고 가정

                    // 이건 단순 변수
                    val name = response.name
                    val token = response.token
                    Log.d("AuthViewModel", "로그인한 사용자: $name / $token ")

                    // ✅ FCM 토큰 전송
                    try {
                        val fcmToken = FirebaseMessaging.getInstance().token.await()
                        authRepository.registerDeviceToken(fcmToken)
                    } catch (e: Exception) {
                        Log.e("AuthViewModel", "FCM 토큰 가져오기 실패", e)
                    }

                } else {
                    _loginResult.value = false
                    _loginError.value = "로그인에 실패했습니다."
                }

            } catch (e: Exception) {
                Log.e("AuthViewModel", "login exception: ${e.localizedMessage}", e)
                _loginResult.value = false
                _loginError.value = e.localizedMessage ?: "알 수 없는 오류"
            }
        }
    }

    // ✅ 회원가입
    fun signup(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val success = authRepository.signupAndGetResponse(name, email, password)
                _signupResult.value = success
            } catch (e: Exception) {
                _signupResult.value = false
                _errorMessage.value = e.message ?: "알 수 없는 오류"
            }
        }
    }

    fun checkEmailDuplication(email: String) {
        viewModelScope.launch {
            try {
                val result = authRepository.checkEmailDuplication(email)
                _isEmailDuplicated.value = result
            } catch (e: Exception) {
                Log.e("AuthViewModel", "checkEmailDuplication exception: ${e.localizedMessage}", e)
                _isEmailDuplicated.value = false
                _errorMessage.value = e.message ?: "이메일 중복 확인 중 오류가 발생했습니다."
            }
        }

    }

    suspend fun refreshAccessToken(refreshToken: String): String? {
        return try {
            val response = authRepository.refreshAccessToken(refreshToken)
            response // ✅ 바로 반환
        } catch (e: Exception) {
            null
        }
    }

    // ✅ 공유자가 초대 코드 요청 후 저장
    fun requestInviteCode(familyName: String) {
        viewModelScope.launch {
            try {
                val response = authRepository.requestInviteCode(familyName)
                val code = response?.inviteCode
                _inviteCode.value = code // 코드값 리턴
            } catch (e: Exception) {
                Log.e("AuthViewModel", "초대코드 요청 실패: ${e.localizedMessage}", e)
                _inviteCode.value = null  // 실패 시 명확하게 null 처리
            }
        }
    }

    // ✅ 피공유자가 초대 코드 전송
    fun transferInviteCode(inviteCode: String) {
        viewModelScope.launch {
            try {
                val result = authRepository.transferInviteCode(inviteCode)
                _transferInviteCodeResult.value = result
            } catch (e: Exception) {
                Log.e("AuthViewModel", "초대 코드 전송 실패: ${e.localizedMessage}", e)
                _transferInviteCodeResult.value = false
            }
        }
    }

    // ✅ 피공유자가 수락 여부 확인 (user 정보는 토큰으로 자동 처리됨)
    suspend fun checkInviteStatus(): TransferInviteResponse? {
        return try {
            val result = authRepository.checkInviteStatus()
            _inviteStatus.value = result
            result
        } catch (e: Exception) {
            _inviteStatus.value = null
            null
        }
    }


    // ✅ 공유자가 수락/거절
    suspend fun acceptGroup(isAccepted: Boolean, requestId: Long): Boolean {
        return authRepository.acceptGroup(requestId, isAccepted)
    }


    fun registerDeviceToken(fcmToken: String) {
        viewModelScope.launch {
            try {
                authRepository.registerDeviceToken(fcmToken)
            } catch (e: Exception) {
                Log.e("FCM", "기기 토큰 전송 실패: ${e.message}")
            }
        }
    }

    fun fetchJoinRequestList() {
        viewModelScope.launch {
            val result = authRepository.getJoinRequestList()
            if (result != null) {
                _joinRequestList.value = result.joinList
                _errorMessage.value = null
            } else {
                _errorMessage.value = "가입 요청 목록을 불러오지 못했습니다."
            }
        }
    }


    fun clearForceLogoutFlag() {
        tokenManager.clearForceLogoutFlag()
    }

    fun monitorForceLogout() {
        if (monitoringJob?.isActive == true) return

        monitoringJob = viewModelScope.launch {
            while (true) {
                if (tokenManager.shouldForceLogout()) {
                    _shouldForceLogout.postValue(true)
                    break
                }
                delay(2000) // 2초마다 감시
            }
        }
    }

    fun updateFamilyName(newName: String) {
        viewModelScope.launch {
            val request = UpdateFamilyNameRequest(newName)
            val response = authRepository.updateFamilyName(request)
            _updateFamilyNameResponse.value = response
        }
    }

}


