package main_oper_except_emotion.ui.invite

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.main_oper_except_emotion.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.viewmodel.AuthViewModel
import javax.inject.Inject


@AndroidEntryPoint
class InvitePopupActivity : AppCompatActivity() {

    private var inviteRequestId: Long = 0
    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var tokenManager: TokenManager

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_popup)

        val inviteRequestIdStr = intent.getStringExtra("invite_request_id")
        val senderName = intent.getStringExtra("sender_name") ?: "상대방"

        val inviteRequestId = inviteRequestIdStr?.toIntOrNull() ?: run {
            finish()
            return
        }

        val tvMessage = findViewById<TextView>(R.id.tvInviteMessage)
        val btnAccept = findViewById<Button>(R.id.btnAccept)
        val btnReject = findViewById<Button>(R.id.btnReject)

        tvMessage.text = "$senderName 님의 초대를 수락하시겠습니까?"

        btnAccept.setOnClickListener {
            handleInviteResponse(true)
        }

        btnReject.setOnClickListener {
            handleInviteResponse(false)
        }
    }

    private fun handleInviteResponse(isAccepted: Boolean) {
        lifecycleScope.launch {
            val success = authViewModel.acceptGroup(isAccepted, inviteRequestId)
            if (!success) {
                Toast.makeText(this@InvitePopupActivity, "요청 처리에 실패했습니다.", Toast.LENGTH_SHORT)
                    .show()
            }
            finish()
        }
    }
}