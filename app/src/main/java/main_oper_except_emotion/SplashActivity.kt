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
    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            kotlinx.coroutines.delay(5000) // 1초간 로고 노출
            startAppFlow()
        }
    }

    private suspend fun startAppFlow() {

        lifecycleScope.launch {
            val sessionValid = tryRefreshIfNeeded()

            if (!sessionValid) {
                tokenManager.clearSession()  // ✅ 전체 초기화
                goToLogin()
                return@launch
            }

            if (tokenManager.isInitialSetupDone()) {
                goToHome()
            } else {
                handleInitialSetupFlow() // ✅ suspend 함수로 변경됨
            }
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
        intent.putExtra("start_destination", R.id.closenessFragment)
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
        // 어차피 승락 이후에, 가족명 수정이 가능한거라, 이대로 둬도 괜찮음
        when {
            hasFamilyName && hasInviteCode -> {
                goToClosenessIntro()
            }

            hasInviteCode && !hasFamilyName -> {
                val inviteStatus = authViewModel.checkInviteStatus()
                when (inviteStatus?.status) {
                    "APPROVED" -> goToHome()
                    "WAITING", null -> goToWaitingPage()
                    "REJECTED" -> {
                        tokenManager.clearSession()
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
