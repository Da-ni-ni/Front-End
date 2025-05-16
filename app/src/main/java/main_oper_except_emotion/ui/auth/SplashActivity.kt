package main_oper_except_emotion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.main_oper_except_emotion.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import main_oper_except_emotion.ui.auth.AuthActivity
import main_oper_except_emotion.viewmodel.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    @Inject lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            kotlinx.coroutines.delay(5000) // 1초간 로고 노출
            startAppFlow()
        }
    }

    private suspend fun startAppFlow() {

        // 리프레시 토큰으로 엑세스 토큰 갱신 여부
        val sessionValid = tryRefreshIfNeeded()

        // 리프레시 토큰 만료
        if (!sessionValid) {
            // 토큰 초기화
            tokenManager.clearSession()
            goToLogin()
            return
        }

        // 친밀도 테스트 완료시 true로 저장됨
        if (tokenManager.isInitialSetupDone()) {
            goToHome()

            // 친밀도 테스트까지 완료 못했을 경우.
        } else {
            handleInitialSetupFlow()
        }
    }

    private suspend fun tryRefreshIfNeeded(): Boolean {
        if (tokenManager.shouldForceLogout()) return false

        val expired = tokenManager.isAccessTokenExpired()
        val refreshToken = tokenManager.getRefreshToken()

        if (!expired) return true
        if (refreshToken.isNullOrEmpty()) return false

        return try {
            val newAccessToken = authViewModel.refreshAccessToken(refreshToken)

            if (!newAccessToken.isNullOrBlank()) {
                tokenManager.saveTokensOnly(newAccessToken, refreshToken)
                tokenManager.clearForceLogoutFlag()
                true
            } else {
                tokenManager.setForceLogout(true)
                false
            }
        } catch (e: Exception) {
            // ✅ 예외 로깅 추가
            Log.e("SplashActivity", "Token refresh 실패: ${e.message}", e)
            tokenManager.setForceLogout(true)
            false
        }
    }

    private fun goToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun goToLogin() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    private fun goToClosenessIntro() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.putExtra("start_destination", R.id.closenessIntroductionFragment)
        startActivity(intent)
        finish()
    }

    private fun goToFamilyInvitePage() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.putExtra("start_destination", R.id.inviteFragment)
        startActivity(intent)
        finish()
    }

    private fun goToWaitingPage() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.putExtra("start_destination", R.id.waitInviteFragment)
        startActivity(intent)
        finish()
    }

    // ✅ suspend 함수로 변경됨 (중첩 launch 제거)
    private suspend fun handleInitialSetupFlow() {
        val hasFamilyName = !tokenManager.getFamilyName().isNullOrBlank()
        val hasInviteCode = !tokenManager.getInviteCode().isNullOrBlank()

        // 초대 코드를 저장했다는 것은
        // #1 공유 받은 초대 코드를 입력해서, 로컬에 저장했다.
        // #2 직접 생성한 초대 코드를 로컬에 저장했다.
        // 가족명을 로컬에 저장하고 있는 것은 최초 생성자임
        // 어차피 가족명이 표시되는 건,
        // 어차피 초대 승락 이후에나, 가족명 수정이 가능한거라, 이대로 둬도 괜찮음
        // 즉 피공유자가 이름이 없다는 것은 초대 승락을 받지 않은 상태라는 것
        when {
            hasFamilyName && hasInviteCode -> {
                goToClosenessIntro()
            }

            // 초대 상태를 체크한다.(이거 체크한다는거 부터가, 초대 요청 보내고, waitinng에서 기다리다가 앱을 종료했다는거임
            // splash에서 바로, 초대 여부를 확인
            // 승인 되면, 친밀도 테스트 인트로
            // 대기 중이면 대기 페이지로
            // 거절이면 초대 코드 입력 페이지로 넘어가서, 다시 요청하게 만듦
            hasInviteCode && !hasFamilyName -> {
                val inviteStatus = authViewModel.checkInviteStatus()
                when (inviteStatus?.status) {
                    "APPROVED" -> goToClosenessIntro()
                    "WAITING", null -> goToWaitingPage()
                    "REJECTED" -> {
                        goToFamilyInvitePage()
                    }
                }
            }

            else -> {
                goToFamilyInvitePage()
            }
        }
    }
}
