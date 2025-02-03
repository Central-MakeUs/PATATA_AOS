package com.cmc.data.di

import android.util.Log
import com.cmc.data.feature.auth.TokenStorage
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor (
    private val tokenStorage: TokenStorage,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // "No-Auth" 헤더가 포함된 요청은 Access Token을 삽입하지 않음
        if (request.header("No-Auth") != null) {
            val newRequest = request.newBuilder()
                .removeHeader("No-Auth") // 헤더를 제거하여 서버에 전달되지 않도록 함
                .build()
            return chain.proceed(newRequest)
        }

        // "Refresh" 헤더가 포함된 요청은 RefreshToken 을 삽입
        if (request.header("Refresh") != null) {
            val refreshToken = runBlocking { tokenStorage.getRefreshToken() }
            val newRequest = request.newBuilder()
                .removeHeader("Refresh")
                .addHeader("RefreshToken", "Bearer $refreshToken")
                .build()
            return chain.proceed(newRequest)
        }

        // Access Token 삽입
        val accessToken = runBlocking { tokenStorage.getAccessToken() }
        val authenticatedRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}