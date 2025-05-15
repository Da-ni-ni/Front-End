package main_oper_except_emotion.repsoitory

import android.util.Log

import com.example.appdanini.data.model.request.invite.GetJoinStatusListResponse
import com.example.appdanini.data.model.request.invite.InviteCodeRequest
import com.example.appdanini.data.model.request.invite.InviteCodeResponse
import com.example.appdanini.data.model.request.invite.TransferInviteRequest
import com.example.appdanini.data.model.request.invite.TransferInviteResponse
import com.example.appdanini.data.model.request.invite.UpdateFamilyNameRequest
import com.example.appdanini.data.model.request.invite.UpdateFamliyNameResponse

import com.google.gson.Gson
import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.network.AuthApi
import main_oper_except_emotion.requestandresponse.auth.EmailCheckRequest
import main_oper_except_emotion.requestandresponse.auth.LoginRequest
import main_oper_except_emotion.requestandresponse.auth.LoginResponse
import main_oper_except_emotion.requestandresponse.auth.SignupRequest
import main_oper_except_emotion.requestandresponse.invite.AcceptGroupRequest
import main_oper_except_emotion.requestandresponse.invite.FcmTokenRequest
import main_oper_except_emotion.util.ErrorResponseDto
import retrofit2.Response
import javax.inject.Inject



class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {

    // ✅ 로그인 요청 후 헤더에서 토큰만 추출하는 방식
    // 서버와 직접 통신하는 레포지토리
    // 응답값을 사용해야 하므로 응답형
    suspend fun loginAndGetResponse(email: String, password: String): LoginResponse? {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                // 응답값
                val body = response.body()
                if (body?.token.isNullOrBlank()) {
                    Log.e("AuthRepository", "토큰 없음")
                    null
                } else {
                    tokenManager.saveTokensOnly(body!!.token, body.refreshToken ?: "")
                    tokenManager.saveMynameAndEmail(body.name,body.email)
                    body // ✅ 전체 LoginResponse 반환
                }
            } else {
                Log.e("AuthRepository", "로그인 실패: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "예외: ${e.localizedMessage}")
            null
        }
    }


    suspend fun signupAndGetResponse(name: String, email: String, password: String): Boolean {
        return try {
            val response = authApi.signup(SignupRequest(name, email, password))
            if (response.isSuccessful) {
                true
            } else {
                val errorBody = response.errorBody()?.string()
                val error = errorBody?.let {
                    Gson().fromJson(it, ErrorResponseDto::class.java)
                }
                Log.e("AuthRepository", "회원가입 실패: ${error?.message}")
                throw Exception(error?.message ?: "회원가입 실패")
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Signup exception: ${e.localizedMessage}")
            throw e // ViewModel에서 catch 해서 메시지 표시 가능
        }
    }

    // 반환 타입 중요
    suspend fun refreshAccessToken(refreshToken: String): String {
        return try {
            val response = authApi.refreshAccessToken("Bearer $refreshToken")
            if (response.isSuccessful) {
                val newAccessToken = response.body()?.accessToken
                if (!newAccessToken.isNullOrEmpty()) {
                    tokenManager.saveAccessTokenOnly(newAccessToken)
                    Log.d("AuthRepository", "새 액세스 토큰 저장 완료: $newAccessToken")
                    newAccessToken // ✅ 실제로 accessToken 문자열 반환
                } else {
                    Log.e("AuthRepository", "응답 바디에 access token 없음")
                    throw Exception("access token이 비어 있습니다.")
                }
            } else {
                Log.e("AuthRepository", "토큰 갱신 실패: ${response.code()} ${response.message()}")
                throw Exception("토큰 갱신 실패: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "예외 발생: ${e.localizedMessage}")
            throw e  // 상위에서 예외 처리할 수 있도록 던짐
        }
    }

    suspend fun checkEmailDuplication(email: String): Boolean? {
        return try {
            val response = authApi.checkEmail(EmailCheckRequest(email))
            if (response.isSuccessful) {
                response.body()?.duplicated
            } else {
                Log.e("AuthRepository", "이메일 중복 확인 실패: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "이메일 중복 확인 예외: ${e.localizedMessage}")
            null
        }
    }

    // AuthRepository.kt 내부에 추가
    // 최초 생성자의 가족 group_id와 초대 코드가 여기서 저장
    suspend fun requestInviteCode(groupName: String): InviteCodeResponse? {
        return try {
            val response = authApi.requestInviteCode(InviteCodeRequest(groupName))
            if (response.isSuccessful) {
                response.body()?.let { result ->
                    // 서버가 내려준 group_id는 생성자 측에서만 저장
                    tokenManager.saveInviteCode(result.inviteCode)
                    result
                }
            } else {
                Log.e(
                    "AuthRepository",
                    "requestInviteCode failed: code=${response.code()} message=${response.message()}"
                )
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "requestInviteCode exception: ${e.localizedMessage}")
            null
        }
    }



    // 실제 API 호출 + 성공 여부 판단
    suspend fun transferInviteCode(inviteCode: String): Boolean {
        return try {
            val response = authApi.transferInviteCode(TransferInviteRequest(inviteCode))
            // 1) HTTP 상태 코드 검사
            if (!response.isSuccessful) {
                Log.e(
                    "AuthRepository",
                    "transferInviteCode failed: code=${response.code()} message=${response.message()}"
                )
                return false
            }
            // 2) 응답 바디가 있을 때만 request_id 저장하고 true 반환
            // 일단 저장은 해두자. 보내 주는 이유가 있을 거임.
            response.body()?.let { result ->
                true
            } ?: run {
                Log.e("AuthRepository", "transferInviteCode: response body is null")
                false
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "transferInviteCode exception: ${e.localizedMessage}", e)
            false
        }
    }

    // 폴링 상태 체크
    // 상태 체크 후
    suspend fun checkInviteStatus(): TransferInviteResponse? {
        return try {
            val response = authApi.checkInviteStatus()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e(
                    "AuthRepository",
                    "checkInviteStatus failed: code=${response.code()} message=${response.message()}"
                )
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "checkInviteStatus error", e)
            null
        }
    }


    suspend fun acceptGroup(requestId: Long, isAccepted: Boolean): Boolean {
        return try {
            // ✅ 서버 enum: APPROVED / REJECTED
            val status = if (isAccepted) "APPROVED" else "REJECTED"
            val request = AcceptGroupRequest(requestId, status)
            val response = authApi.acceptGroup(request)

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("AuthRepository", "수락 결과: requestId=${body?.requestId}, 시간=${body?.updatedAt}")
                true
            } else {
                Log.e("AuthRepository", "초대 응답 실패: ${response.code()} ${response.message()}")
                false
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "초대 응답 예외: ${e.localizedMessage}", e)
            false
        }
    }


    suspend fun registerDeviceToken(fcmToken: String) {
        try {
            val response = authApi.registerDeviceToken(FcmTokenRequest(fcmToken))
            if (response.isSuccessful) {
                Log.d("FCM", "토큰 등록 성공")
            } else {
                Log.e("FCM", "토큰 등록 실패: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("FCM", "토큰 등록 예외: ${e.localizedMessage}")
        }
    }

    suspend fun getJoinRequestList(): GetJoinStatusListResponse? {
        return try {
            val response = authApi.checkJoinStatusList()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("InviteRepository", "getJoinRequestList failed: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("InviteRepository", "getJoinRequestList error", e)
            null
        }
    }
    suspend fun updateFamilyName(request: UpdateFamilyNameRequest): UpdateFamliyNameResponse? {
        return try {
            val response = authApi.updateFamilyName(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    tokenManager.saveFamilyname(body.groupName)
                    tokenManager.saveGroupId(body.groupId)
                    body
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

}
