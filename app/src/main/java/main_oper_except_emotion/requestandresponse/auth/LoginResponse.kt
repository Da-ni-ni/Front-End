package main_oper_except_emotion.requestandresponse.auth

data class LoginResponse(

    val name: String,//회원 암호
    val email: String, //회원 email
    val token : String,
    val refreshToken : String? //// refresh 토큰, 확인 바람

)
