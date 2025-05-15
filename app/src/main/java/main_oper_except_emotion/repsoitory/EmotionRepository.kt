package main_oper_except_emotion.repository


import com.example.appdanini.data.model.remote.network.EmotionApi
import main_oper_except_emotion.requestandresponse.emotion.*
import main_oper_except_emotion.requestandresponse.name.*
import retrofit2.Response
import javax.inject.Inject
class EmotionRepository @Inject constructor(
    private val api: EmotionApi
) {

    // 서버 응답 바디가 없음	Boolean	성공/실패만 확인하면 되므로 간단하게 처리 가능
    // 서버 응답 바디가 있음	Response<T>	body()를 활용해야 하므로 전체 응답을 받아야 함


    suspend fun submitEmotion(request: EmotionSubmitRequest): Response<EmotionSubmitResponse> {
        return api.submitEmotion(request)
    }

    // 감정 수정
    suspend fun updateEmotion(request: UpdateEmotionRequest): Response<UpdateEmotionResponse> {
        return api.updateEmotion(request)
    }

    // 가족 전체 감정 조회
    suspend fun getTodayFamilyEmotions(groupId : Long): Response<List<GetTodayFamilyEmotionResponse>> {
        return api.getTodayFamilyEmotions(groupId)
    }

    // 가족 개인 감정 조회
    suspend fun getPersonalEmotion(emotionId: Long): Response<PersonalEmotionDetailResponse> {
        return api.personalEmotion(emotionId)
    }

    // 가족 그룹 이름 수정
    suspend fun updateFamilyGroupName(request: FamilyGroupNameRequest): Response<FamilyGroupNameResponse> {
        return api.updateFamilyGroupName(request)
    }

    // 내 이름 수정 (Boolean → Response로 변경)
    suspend fun updateName(newName: String): Response<PersonalNameResponse> {
        val request = PersonalNameRequest(newName)
        return api.updateMyName(request)
    }

    // 가족 구성원의 이름을 내가 수정 (Boolean → Response로 변경)
    suspend fun updateOtherMemberName(emotionId: Long, newName: String): Response<PersonalNameResponse> {
        val request = PersonalNameRequest(newName)
        return api.updatePersonalName(emotionId, request)
    }
}
