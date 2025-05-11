package main_oper_except_emotion.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import main_oper_except_emotion.requestandresponse.emotion.Emotion
import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.repository.EmotionRepository
import main_oper_except_emotion.requestandresponse.emotion.EmotionSubmitRequest
import main_oper_except_emotion.requestandresponse.emotion.GetTodayFamilyEmotionResponse
import main_oper_except_emotion.requestandresponse.emotion.UpdateEmotionRequest
import main_oper_except_emotion.requestandresponse.emotion.PersonalEmotionDetailResponse
import main_oper_except_emotion.requestandresponse.name.FamilyGroupNameRequest
import java.time.LocalTime
import javax.inject.Inject


@HiltViewModel
class EmotionViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val repository: EmotionRepository
) : ViewModel() {

    private val _showEmotionPopup = MutableLiveData<Boolean>()
    val showEmotionPopup: LiveData<Boolean> = _showEmotionPopup

    private val _myEmotion = MutableLiveData<Emotion?>()
    val myEmotion: LiveData<Emotion?> = _myEmotion

    private val _submitSuccess = MutableLiveData<Boolean>()
    val submitSuccess: LiveData<Boolean> = _submitSuccess

    private val _familyEmotions = MutableLiveData<List<GetTodayFamilyEmotionResponse>>()
    val familyEmotions: LiveData<List<GetTodayFamilyEmotionResponse>> = _familyEmotions

    private val _nameUpdateSuccess = MutableLiveData<Boolean>()
    val nameUpdateSuccess: LiveData<Boolean> = _nameUpdateSuccess

    private val _familyNameUpdateSuccess = MutableLiveData<Boolean>()
    val familyNameUpdateSuccess: LiveData<Boolean> = _familyNameUpdateSuccess

    private val _familyName = MutableLiveData<String?>()
    val familyName: LiveData<String?> = _familyName

    private val _personalEmotionDetail = MutableLiveData<PersonalEmotionDetailResponse>()
    val personalEmotionDetail: LiveData<PersonalEmotionDetailResponse> = _personalEmotionDetail

    private val _currentEmotion = MutableLiveData<Emotion>(Emotion.JOY) // 기본 감정 설정
    val currentEmotion: LiveData<Emotion> get() = _currentEmotion


    // 팝업창 설정
    @RequiresApi(Build.VERSION_CODES.O)
    fun checkEmotionPopupNeeded() {
        val submittedToday = tokenManager.hasSubmittedToday()
        val now = LocalTime.now()
        val isAfter6AM = now.isAfter(LocalTime.of(6, 0))
        _showEmotionPopup.value = !submittedToday || isAfter6AM
    }

    // 감정 등록
    @RequiresApi(Build.VERSION_CODES.O)
    fun submitEmotion(emotion: Emotion) {
        _myEmotion.value = emotion

        viewModelScope.launch {
            try {
                val response = repository.submitEmotion(EmotionSubmitRequest(emotion))
                if (response.isSuccessful) {
                    tokenManager.markEmotionSubmittedNow()
                    _submitSuccess.value = true
                    Log.d("EmotionViewModel", "등록 성공: ${response.body()?.createdAt}")
                } else {
                    Log.e("EmotionViewModel", "감정 등록 실패: ${response.code()} ${response.errorBody()?.string()}")
                    _submitSuccess.value = false
                }
            } catch (e: Exception) {
                Log.e("EmotionViewModel", "감정 등록 오류: ${e.localizedMessage}")
                _submitSuccess.value = false
            }
        }
    }

    fun setMyEmotion(emotion: Emotion) {
        _currentEmotion.value = emotion // 감정 상태 저장
    }

    fun getCurrentEmotion(): Emotion {
        return _currentEmotion.value ?: Emotion.JOY // 기본값 반환
    }



    // 가족 감정 불러오기
    @RequiresApi(Build.VERSION_CODES.O)
    fun loadFamilyEmotions() {
        viewModelScope.launch {
            try {
                val response = repository.getTodayFamilyEmotions()
                if (response.isSuccessful) {
                    val responseList = response.body() ?: emptyList()
                    _familyEmotions.value = responseList

                    // ✅ 닉네임 캐싱
                    responseList.forEach { member ->
                        val nickname = member.nickname ?: member.name
                        tokenManager.saveNickname(member.user_id, nickname)
                    }

                    // ✅ 내 감정 따로 추출 (getUserIdFromToken 사용)
                    val myId = tokenManager.getUserIdFromToken()
                    if (myId != null) {
                        // 내 감정 객체 찾기
                        val myEmotionObj = responseList.firstOrNull { it.user_id == myId }
                        _myEmotion.value = myEmotionObj?.emotion
                    } else {
                        Log.w("EmotionViewModel", "userId 디코딩 실패 → 내 감정 분리 불가")
                        _myEmotion.value = null  // 혹은 UI에서 적절한 기본값 처리
                    }
                } else {
                    Log.e("EmotionViewModel", "가족 감정 불러오기 실패: ${response.code()} ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("EmotionViewModel", "가족 감정 요청 오류: ${e.localizedMessage}")
            }
        }
    }

    // 감정 수정
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateEmotion(emotion: Emotion) {
        _myEmotion.value = emotion              // ✅ UI에 즉시 반영 (옵티미스틱)

        viewModelScope.launch {
            try {
                val name = tokenManager.getUserName()
                val request = UpdateEmotionRequest(name = name, emotion = emotion)
                val response = repository.updateEmotion(request)
                if (!response.isSuccessful) {
                    Log.e("EmotionViewModel", "감정 수정 실패: ${response.code()} ${response.errorBody()?.string()}")
                    // _myEmotion.value = previousEmotion // ❌ 실패 시 롤백
                }
            } catch (e: Exception) {
                Log.e("EmotionViewModel", "감정 수정 오류: ${e.localizedMessage}")
                // _myEmotion.value = previousEmotion  // ❌ 예외 발생 시 롤백
            }
        }
    }

    // 개인 감정 조회
    fun fetchPersonalEmotion(user_id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getPersonalEmotion(user_id)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _personalEmotionDetail.value = it
                    }
                } else {
                    Log.e("EmotionViewModel", "개인 감정 조회 실패: ${response.code()} ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("EmotionViewModel", "개인 감정 요청 오류: ${e.localizedMessage}")
            }
        }
    }



    // 내 이름 수정
    fun updateMyName(name: String) {
        tokenManager.saveUserName(name)         // ✅ 즉시 반영
        _nameUpdateSuccess.value = true         // UI 성공 처리 (옵티미스틱)

        viewModelScope.launch {
            try {
                val response = repository.updateName(name)
                if (!response.isSuccessful) {
                    Log.e("EmotionViewModel", "이름 수정 실패: ${response.code()} ${response.errorBody()?.string()}")
                    _nameUpdateSuccess.value = false
                }
            } catch (e: Exception) {
                Log.e("EmotionViewModel", "이름 수정 오류: ${e.localizedMessage}")
                _nameUpdateSuccess.value = false
            }
        }
    }

    // 가족 그룹 이름 수정
    fun updateFamilyGroupName(new_name: String) {
        tokenManager.saveFamilyname(new_name)     // ✅ 즉시 반영
        _familyName.value = new_name
        _familyNameUpdateSuccess.value = true    // UI 성공 처리 (옵티미스틱)

        viewModelScope.launch {
            try {
                // 서버에서 응답 받기
                val response = repository.updateFamilyGroupName(FamilyGroupNameRequest(new_name))
                if (response.isSuccessful) {
                    // 성공적으로 변경된 경우
                    _familyNameUpdateSuccess.value = true
                } else {
                    // 실패한 경우, 응답 코드 및 메시지로 실패 원인 기록
                    Log.e("EmotionViewModel", "가족명 서버 반영 실패: ${response.code()} ${response.errorBody()?.string()}")
                    _familyNameUpdateSuccess.value = false
                }
            } catch (e: Exception) {
                // 예외 발생 시 처리
                Log.e("EmotionViewModel", "가족명 서버 오류: ${e.localizedMessage}")
                _familyNameUpdateSuccess.value = false
            }
        }
    }

    // 가족 구성원 이름 수정
    fun updateOtherMemberName(userId: Int, new_name: String) {
        tokenManager.saveNickname(userId, new_name)    // ✅ 옵티미스틱 반영
        _nameUpdateSuccess.value = true

        viewModelScope.launch {
            try {
                // 서버에서 응답 받기
                val response = repository.updateOtherMemberName(userId, new_name)
                if (response.isSuccessful) {
                    _nameUpdateSuccess.value = true  // 이름 수정 성공
                } else {
                    Log.e("EmotionViewModel", "가족 구성원 이름 수정 실패: ${response.code()} ${response.errorBody()?.string()}")
                    _nameUpdateSuccess.value = false  // 실패 시 실패 처리
                }
            } catch (e: Exception) {
                Log.e("EmotionViewModel", "이름 수정 오류: ${e.localizedMessage}")
                _nameUpdateSuccess.value = false  // 예외 발생 시 실패 처리
            }
        }
    }


}

