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

    // 내 감정 추가
    @Headers("Need-Auth: true")
    @POST("/api/v1/emotions")
    suspend fun submitEmotion(
        @Body request: EmotionSubmitRequest
    ): Response<EmotionSubmitResponse>

    // 내 감정 수정
    @Headers("Need-Auth: true")
    @PUT("/api/v1/emotions")
    suspend fun updateEmotion(
        @Body request: UpdateEmotionRequest
    ): Response<UpdateEmotionResponse>

    // 가족 개인 감정 조회
    // 감정 목록을 받을 때 유저 아이디를 받게 되고, 그걸 쓰면 될듯
    @Headers("Need-Auth: true")
    @GET("/api/v1/emotions/{emotionId}")
    suspend fun personalEmotion(
        @Path("emotionId") emotionId: Long
    ): Response<PersonalEmotionDetailResponse>

    // 가족 전체 감정 조회
    @Headers("Need-Auth: true")
    @GET("/api/v1/emotions/group/{groupId}")

    suspend fun getTodayFamilyEmotions(
        @Path("groupId") groupId: Long
    ): Response<List<GetTodayFamilyEmotionResponse>>



    // 가족 구성원명 수정
    // 마찬가지로 homefragment에서 user_id 활용
    @Headers("Need-Auth: true")
    @PUT("/api/v1/emotions/{emotionId}")
    suspend fun updatePersonalName(
        @Path("userId") userId: Long,
        @Body request: PersonalNameRequest
    ): Response<PersonalNameResponse>

    //---------------------------------------------------------------
    // 확인 필요

    // 가족명 수정
    @Headers("Need-Auth: true")
    @PUT("/api/v1/groups")
    suspend fun updateFamilyGroupName(
        @Body request: FamilyGroupNameRequest
    ): Response<FamilyGroupNameResponse>



    // 내 이름 수정
    @Headers("Need-Auth: true")
    @PUT("/api/v1/emotions_name")
    suspend fun updateMyName(
        @Body request: PersonalNameRequest
    ) : Response<PersonalNameResponse>

}
