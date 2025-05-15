package main_oper_except_emotion.requestandresponse.emotion

import android.telecom.DisconnectCause.MISSED
import com.example.main_oper_except_emotion.R
import com.google.gson.annotations.SerializedName


enum class EmotionType(val label: String) {
    @SerializedName("기쁨")
    HAPPY("기쁨"),

    @SerializedName("피곤함")
    TIRED("피곤함"),

    @SerializedName("편안함")
    RELAXED("편안함"),

    @SerializedName("슬픔")
    SAD("슬픔"),

    @SerializedName("분노")
    ANGRY("분노"),

    @SerializedName("보고픔")
    MISSING("보고픔");

    companion object {
        fun from(label: String): EmotionType? {
            return values().find { it.label == label }
        }
    }

}

fun getImageRes(emotion: EmotionType): Int {
    return when (emotion) {
        EmotionType.HAPPY -> R.drawable.ic_joy
        EmotionType.SAD -> R.drawable.ic_sad
        EmotionType.MISSING -> R.drawable.ic_missed
        EmotionType.TIRED -> R.drawable.ic_tired
        EmotionType.ANGRY -> R.drawable.ic_angry
        EmotionType.RELAXED -> R.drawable.ic_comfort
    }
}