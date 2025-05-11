package com.example.appdanini.util

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

import dagger.Lazy
import main_oper_except_emotion.TokenManager

class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // 테스트용: 인증 실패 시 재시도하지 않고 로그아웃 처리
        tokenManager.setForceLogout(true)
        return null
    }
}