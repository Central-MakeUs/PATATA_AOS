package com.cmc.data.di

import com.cmc.data.preferences.TokenPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor (
    private val tokenPreferences: TokenPreferences,
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
            val refreshToken = tokenPreferences.getCachedRefreshToken()
            val newRequest = request.newBuilder()
                .removeHeader("Refresh")
                .addHeader("RefreshToken", "Bearer $refreshToken")
                .build()
            return chain.proceed(newRequest)
        }

        // "Google" 헤더가 포함된 요청은 GoogleAccessToken 을 추가 삽입
        if (request.header("Google") != null) {
            val accessToken = tokenPreferences.getCachedAccessToken()
            val googleAccessToken = tokenPreferences.getCachedGoogleAccessToken()

            val newRequest = request.newBuilder()
                .removeHeader("Google")
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("google-accessToken", "$googleAccessToken")
                .build()
            return chain.proceed(newRequest)
        }


        // Access Token 삽입
        val accessToken = runBlocking { tokenPreferences.getAccessToken() }
        val authenticatedRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}