package main_oper_except_emotion.requestandresponse.auth

data class LoginRequest(
    val email: String, //회원 email
    val password: String//회원 암호
)
