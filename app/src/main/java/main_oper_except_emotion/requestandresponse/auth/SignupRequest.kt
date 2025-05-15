package main_oper_except_emotion.requestandresponse.auth

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String
)
