package main_oper_except_emotion.requestandresponse.emotion

import com.example.main_oper_except_emotion.R
import com.google.gson.annotations.SerializedName
enum class Emotion(val label: String) {
    @SerializedName("기쁨")
    JOY("기쁨"),

    @SerializedName("피곤")
    TIRED("피곤"),

    @SerializedName("편안함")
    COMFORT("편안함"),

    @SerializedName("슬픔")
    SAD("슬픔"),

    @SerializedName("화남")
    ANGRY("화남"),

    @SerializedName("보고픔")
    MISSED("보고픔");

    companion object {
        fun from(label: String): Emotion? {
            return values().find { it.label == label }
        }

        fun getImageRes(emotion: Emotion): Int {
            return when (emotion) {
                JOY -> R.drawable.ic_joy
                SAD -> R.drawable.ic_sad
                MISSED -> R.drawable.ic_missed
                TIRED -> R.drawable.ic_tired
                ANGRY -> R.drawable.ic_angry
                COMFORT -> R.drawable.ic_comfort
            }
        }
    }
}
