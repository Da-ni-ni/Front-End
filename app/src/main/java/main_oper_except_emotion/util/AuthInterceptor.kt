package com.example.appdanini.util

import main_oper_except_emotion.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject


class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 요청 헤더에서 "Need-Auth" 값을 확인
        val needAuth = originalRequest.header("Need-Auth")?.toBoolean() ?: false
        val newRequestBuilder = originalRequest.newBuilder()

        if (needAuth) {
            val accessToken = tokenManager.getAccessToken()
            accessToken?.let {
                newRequestBuilder.addHeader("Authorization", "Bearer $it")
            }
        }

        // "Need-Auth" 헤더는 실제 서버에 보내지 않게 제거
        newRequestBuilder.removeHeader("Need-Auth")

        return chain.proceed(newRequestBuilder.build())
    }
}

