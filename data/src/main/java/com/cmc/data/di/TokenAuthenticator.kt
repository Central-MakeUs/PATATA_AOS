package com.cmc.data.di

import com.cmc.data.base.TokenDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenDataSource: TokenDataSource,
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // 무한 루프 방지: 이미 Authorization 헤더가 있는 경우 null 반환
        if (response.request.header("Authorization") != null) {
            val refreshResult = runBlocking {
                tokenDataSource.refreshAccessToken()
            }

            return if (refreshResult.isSuccess) {
                val newAccessToken = runBlocking { tokenDataSource.getAccessToken() }

                // 새 액세스 토큰으로 요청 재시도
                response.request.newBuilder()
                    .removeHeader("Authorization")
                    .addHeader("Authorization", "Bearer $newAccessToken")
                    .build()
            } else {
                // 리프레시 실패 시 null 반환 → 자동 로그아웃 처리
                null
            }
        }

        return null
    }
}