package com.example.appdanini.data.model.remote.network

import main_oper_except_emotion.requestandresponse.emotion.EmotionSubmitRequest
import main_oper_except_emotion.requestandresponse.emotion.EmotionSubmitResponse
import main_oper_except_emotion.requestandresponse.emotion.GetTodayFamilyEmotionResponse
import main_oper_except_emotion.requestandresponse.name.FamilyGroupNameRequest
import main_oper_except_emotion.requestandresponse.name.PersonalNameRequest
import main_oper_except_emotion.requestandresponse.emotion.UpdateEmotionRequest
import main_oper_except_emotion.requestandresponse.emotion.UpdateEmotionResponse
import main_oper_except_emotion.requestandresponse.name.FamilyGroupNameResponse
import main_oper_except_emotion.requestandresponse.emotion.PersonalEmotionDetailResponse
import main_oper_except_emotion.requestandresponse.name.PersonalNameResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface EmotionApi {

    @Headers("Need-Auth: true")
    @POST("/api/v1/emotions")
    suspend fun submitEmotion(
        @Body request: EmotionSubmitRequest
    ): Response<EmotionSubmitResponse>

    @Headers("Need-Auth: true")
    @PUT("/api/v1/emotions")
    suspend fun updateEmotion(
        @Body request: UpdateEmotionRequest
    ): Response<UpdateEmotionResponse>

    // 가족 전체 감정 조회
    @Headers("Need-Auth: true")
    @GET("/api/v1/emotions")
    suspend fun getTodayFamilyEmotions(
    ): Response<List<GetTodayFamilyEmotionResponse>>


    // 가족 개인 감정 조회
    // 감정 목록을 받을 때 유저 아이디를 받게 되고, 그걸 쓰면 될듯
    @Headers("Need-Auth: true")
    @GET("/api/v1/emotions/{userId}")
    suspend fun personalEmotion(
        @Path("userId") user_id: Int
    ): Response<PersonalEmotionDetailResponse>

    // 가족명 수정
    @Headers("Need-Auth: true")
    @PUT("/api/v1/groups")
    suspend fun updateFamilyGroupName(
        @Body request: FamilyGroupNameRequest
    ): Response<FamilyGroupNameResponse>


    // 가족 구성원명 수정
    // 마찬가지로 homefragment에서 user_id 활용
    @Headers("Need-Auth: true")
    @PUT("/api/v1/emotions/{userId}")
    suspend fun updatePersonalName(
        @Path("userId") user_id: Int,
        @Body request: PersonalNameRequest
    ): Response<PersonalNameResponse>

    // 내 이름 수정
    @Headers("Need-Auth: true")
    @PUT("/api/v1/emotions_name")
    suspend fun updateMyName(
        @Body request: PersonalNameRequest
    ) : Response<PersonalNameResponse>

}
