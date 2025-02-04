package com.cmc.data.feature.token.repository


import android.content.Context
import com.cmc.data.base.TokenApiService
import com.cmc.data.di.TokenRefreshApi
import com.cmc.data.preferences.TokenPreferences
import com.cmc.domain.base.exception.ApiException
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @TokenRefreshApi private val tokenApiService: TokenApiService,
    private val tokenPreferences: TokenPreferences,
): TokenRepository {

    override suspend fun refreshAccessToken(): Result<Unit> {
        return try {
            val response = tokenApiService.refreshAccessToken()

            if (response.isSuccess && response.result != null) {
                val newAccessToken = response.result.accessToken
                val newRefreshToken = response.result.refreshToken
                tokenPreferences.saveTokens(newAccessToken, newRefreshToken)
                Result.success(Unit)
            } else {
                Result.failure(ApiException.TokenExpired("토큰이 만료되었습니다."))
            }
        } catch (e: Exception) {
            Result.failure(ApiException.ServerError("서버 오류 발생: ${e.message}"))
        }
    }


    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        tokenPreferences.saveTokens(accessToken, refreshToken)
    }

    override suspend fun getAccessToken(): String? {
        return tokenPreferences.getAccessToken()
    }

    override suspend fun clearTokens() {
        tokenPreferences.clearTokens()
    }

}