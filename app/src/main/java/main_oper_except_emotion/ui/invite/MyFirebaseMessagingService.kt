package main_oper_except_emotion.ui.invite

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import main_oper_except_emotion.network.AuthApi
import main_oper_except_emotion.requestandresponse.invite.FcmTokenRequest
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var authApi: AuthApi

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val type = remoteMessage.data["type"]
        if (type == "invite_request") {
            val inviteRequestId = remoteMessage.data["request_id"] ?: return
            val senderName = remoteMessage.data["sender_name"] ?: "상대방"
            showInvitePopup(inviteRequestId, senderName)
        }
    }

    private fun showInvitePopup(inviteRequestId: String, senderName: String) {
        val intent = Intent(this, InvitePopupActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("invite_request_id", inviteRequestId)
            putExtra("sender_name", senderName)
        }
        startActivity(intent)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = authApi.registerDeviceToken(FcmTokenRequest(token))

                if (response.isSuccessful) {
                    Log.d("FCM", "서버에 토큰 등록 성공")
                } else {
                    Log.e("FCM", "서버에 토큰 등록 실패: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("FCM", "토큰 전송 예외: ${e.localizedMessage}")
            }
        }
    }
}
