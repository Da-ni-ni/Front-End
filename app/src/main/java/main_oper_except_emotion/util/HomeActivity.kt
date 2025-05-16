package main_oper_except_emotion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.main_oper_except_emotion.R
import com.example.main_oper_except_emotion.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import main_oper_except_emotion.ui.auth.AuthActivity
import main_oper_except_emotion.util.TokenManager
import main_oper_except_emotion.viewmodel.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBottomNavigation()
        observeForceLogout()
        authViewModel.monitorForceLogout()
    }

    private fun observeForceLogout() {
        authViewModel.shouldForceLogout.observe(this) { shouldLogout ->
            if (shouldLogout == true) {
                Toast.makeText(this, "세션이 만료되었습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, AuthActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setBottomNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_home)
                    as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationHome.setupWithNavController(navController)
    }
}