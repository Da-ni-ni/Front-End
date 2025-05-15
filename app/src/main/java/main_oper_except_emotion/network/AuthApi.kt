package main_oper_except_emotion.network

import com.example.appdanini.data.model.request.invite.GetJoinStatusListResponse
import com.example.appdanini.data.model.request.invite.InviteCodeRequest
import com.example.appdanini.data.model.request.invite.InviteCodeResponse
import com.example.appdanini.data.model.request.invite.TransferInviteRequest
import com.example.appdanini.data.model.request.invite.TransferInviteResponse
import com.example.appdanini.data.model.request.invite.UpdateFamilyNameRequest
import com.example.appdanini.data.model.request.invite.UpdateFamliyNameResponse
import main_oper_except_emotion.requestandresponse.auth.EmailCheckRequest
import main_oper_except_emotion.requestandresponse.auth.EmailCheckResponse
import main_oper_except_emotion.requestandresponse.auth.LoginRequest
import main_oper_except_emotion.requestandresponse.auth.LoginResponse
import main_oper_except_emotion.requestandresponse.auth.RefreshTokenResponse
import main_oper_except_emotion.requestandresponse.auth.SignupRequest
import main_oper_except_emotion.requestandresponse.invite.AcceptGroupRequest
import main_oper_except_emotion.requestandresponse.invite.AcceptGroupResponse
import main_oper_except_emotion.requestandresponse.invite.FcmTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT


interface AuthApi {

    // #1 회원가입 (인증 불필요)
    // 확인
    @POST("/api/v1/users/signup")
    suspend fun signup(@Body req: SignupRequest): Response<Void>

    // #2 로그인 (인증 불필요)
    // 확인, 이때 받는 이름과 이메일은 저장
    // 그룹 아이디는 널값으로 받을 수 있음을 인지하기
    @POST("/api/v1/users/login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>


    // #3 이메일 중복 확인 (인증 불필요)
    // # 확인
    @POST("/api/v1/users/check-email")
    suspend fun checkEmail(@Body request: EmailCheckRequest): Response<EmailCheckResponse>

    //         ========== 인증 필요한 API에는 모두 Need-Auth 추가 ==========

    //#5 초대코드 생성
    //# 확인
    @Headers("Need-Auth: true")
    @POST("/api/v1/groups")
    suspend fun requestInviteCode(@Body req : InviteCodeRequest) : Response<InviteCodeResponse>

    // #6 가입 요청
    // # 확인
    @Headers("Need-Auth: true")
    @POST("/api/v1/groups/join-request")
    suspend fun transferInviteCode(@Body req: TransferInviteRequest): Response<TransferInviteResponse>

    // fcm 메세지를 이용하면 request_id를 받고, 보내주는 것도 가능함.

    // #8 공유자 가입 수락
    @POST("/api/v1/groups/join-accept")
    @Headers("Need-Auth: true")
    suspend fun acceptGroup(
        @Body request: AcceptGroupRequest
    ): Response<AcceptGroupResponse>

    // #10 가입 요청 목록 조회
    @Headers("Need-Auth: true")
    @GET("/api/v1/groups/join-request")
    suspend fun checkJoinStatusList(): Response<GetJoinStatusListResponse>

    // #11 피공유자의 가족명 수정
    @Headers("Need-Auth: true")
    @PUT("/api/v1/groups")
    suspend fun updateFamilyName(@Body request : UpdateFamilyNameRequest) : Response<UpdateFamliyNameResponse>

    // #7 초대 요청 상태 체크(피공유자, 폴링)
    // # 수정 필요
    @Headers("Need-Auth: true")
    @GET("/api/v1/groups/request/me")
    suspend fun checkInviteStatus(): Response<TransferInviteResponse>

    //-----------------------------------------------------------------
    // 확인 바람
    // #9 디바이스 토큰 등록
    @Headers("Need-Auth: true")
    @POST("/api/v1/users/device-token")
    suspend fun registerDeviceToken(@Body request: FcmTokenRequest): Response<Unit>


    // #4 토큰 리프레시
    // # 확인
    @POST("/api/v1/users/refresh")
    suspend fun refreshAccessToken(
        @Header("Authorization") refreshToken: String
    ): Response<RefreshTokenResponse>


}