package main_oper_except_emotion

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import main_oper_except_emotion.requestandresponse.emotion.Emotion
import org.json.JSONObject
import java.time.LocalDate
import java.util.Base64

class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "auth_prefs"
        private const val ACCESS_TOKEN_KEY = "access_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val FORCE_LOGOUT_KEY = "force_logout"
        private const val FAMILY_NAME_KEY = "family_name"
        private const val INVITE_CODE_KEY = "inviteCode"
        private const val INITIAL_SETUP_DONE_KEY = "initial_setup_done" // 🔥 추가
        private const val INVITE_CODE_SUBMITTED_KEY = "invite_code_submitted" // 🔥 추가
        private const val GROUP_SCORE = "group_score"
        private const val PERSONAL_SCORE = "personal_score"
        private const val GROUP_ID = "group_id"
        private const val EMOTION_SUBMIT_DATE_KEY = "emotion_submit_date" // 날짜 기준
        private const val EMOTION_SUBMIT_TIMESTAMP_KEY = "emotion_submit_timestamp" // 시간 기준
        private const val EMOTION_COOLDOWN_HOURS = 12L // 제출 후 다시 뜨게 하려면 몇 시간?
    }

    // "auth_prefs"라는 이름의 전용 저장소(SharedPreferences)를 가져와서, 이후 prefs 변수로 토큰을 읽고 쓰도록 준비하는 것
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


    // # 계정 및 초기 설정 관련 코드들

    // 로그인 정보 저장
    fun saveLoginData(name : String, email : String){
        prefs.edit()
            .putString("name",name)
            .putString("email",email)
    }

    // 토큰 저장
    fun saveTokensOnly(token: String, refreshToken: String) {
        prefs.edit()
            .putString("access_token", token)
            .putString("refresh_token", refreshToken)
            .apply()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getUserIdFromToken(): Int? {
        return try {
            val token = getAccessToken() ?: return null
            val parts = token.split(".")
            if (parts.size < 2) return null
            val payload = parts[1]
            val padded = payload.padEnd(payload.length + (4 - payload.length % 4) % 4, '=')
            val decoded = Base64.getUrlDecoder().decode(padded)
            val json = JSONObject(String(decoded))
            json.getInt("user_id")  // 실제 키 이름은 payload 확인 필요
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 초대 코드 저장
    fun saveInviteCode(inviteCode: String) {
        prefs.edit()
            .putString(INVITE_CODE_KEY, inviteCode)
            .apply()
    }

    // 친밀도 점수 저장
    fun saveScore(group_score: Int, personal_score: Int) {
        prefs.edit()
            .putInt(GROUP_SCORE, group_score)
            .putInt(PERSONAL_SCORE, personal_score)
            .apply()
    }


    fun getUserEmail(): String? = prefs.getString("email", null) // 유저 이메일 얻기
    fun getAccessToken(): String? = prefs.getString(ACCESS_TOKEN_KEY, null) // 토큰 얻기
    fun getRefreshToken(): String? = prefs.getString(REFRESH_TOKEN_KEY, null) // 리스프레시 토큰 얻기


    // 그룹 아이디 저장
    fun saveGroupId(group_id: Int) {
        prefs.edit()
            .putInt(GROUP_ID, group_id)
            .apply()
    }

    // 가족명 저장
    fun saveFamilyname(family_name: String) {
        prefs.edit()
            .putString(FAMILY_NAME_KEY, family_name)
            .apply()
    }


    // 가족들 닉네임
    fun saveNickname(userId: Int, nickname: String) {
        prefs.edit().putString("nickname_$userId", nickname).apply()
    }

    // 닉네임 얻기
    fun getNickname(userId: Int): String? {
        return prefs.getString("nickname_$userId", null)
    }


    // 감정 제출 날짜와 시간을 저장하는 함수
    // 언제 호출?: 사용자가 감정을 선택해서 서버에 제출한 직후
    @RequiresApi(Build.VERSION_CODES.O)
    fun markEmotionSubmittedNow() {
        val now = System.currentTimeMillis()
        val today = LocalDate.now().toString()
        prefs.edit()
            .putLong(EMOTION_SUBMIT_TIMESTAMP_KEY, now)
            .putString(EMOTION_SUBMIT_DATE_KEY, today)
            .apply()
    }

    //역할: 오늘 감정을 이미 제출했는지 확인하는 함수
    //언제 호출?: 앱 시작 시 또는 홈 프래그먼트 진입 시 팝업 띄울지 판단할 때 사
    @RequiresApi(Build.VERSION_CODES.O)
    fun hasSubmittedToday(): Boolean {
        val today = LocalDate.now().toString()
        return prefs.getString(EMOTION_SUBMIT_DATE_KEY, "") == today
    }


    fun saveStartDate(date: String) {
        prefs.edit().putString("family_start_date", date).apply()
    }

    fun getStartDate(): String? {
        return prefs.getString("family_start_date", null)
    }


    // 유저 네임 저장
    fun saveUserName(name: String) {
        prefs.edit().putString("name", name).apply()
    }


    fun getUserName(): String? = prefs.getString("name", null)

    fun getUserId(): String? = prefs.getString("userId", null)

    fun getFamilyName(): String? = prefs.getString(FAMILY_NAME_KEY, null)
    fun getInviteCode(): String? = prefs.getString(INVITE_CODE_KEY, null)
    fun getGroupScore(): Int = prefs.getInt(GROUP_SCORE, 0)
    fun getPersonalScore(): Int = prefs.getInt(PERSONAL_SCORE, 0)
    fun getGroupId(): Int = prefs.getInt(GROUP_ID, 0)

    fun clearTokens() {
        prefs.edit().clear().apply()
    }

    fun setForceLogout(value: Boolean) {
        prefs.edit().putBoolean(FORCE_LOGOUT_KEY, value).apply()
    } //주로 토큰 갱신(refresh) 실패 시(예: 서버에서 401을 받았거나 네트워크 예외) 호출해서, 앱 전역에 “더 이상 이 계정으로 API 호출을 해서는 안 된다”는 신호를 보내는 용도입니다. ​

    fun shouldForceLogout(): Boolean {
        return prefs.getBoolean(FORCE_LOGOUT_KEY, false)
    }

    fun clearForceLogoutFlag() {
        prefs.edit().remove(FORCE_LOGOUT_KEY).apply()
    }
}
