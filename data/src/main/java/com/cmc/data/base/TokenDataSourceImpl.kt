package com.cmc.data.base


import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.cmc.data.di.TokenRefreshApi
import com.cmc.data.feature.auth.TokenStorage
import com.cmc.domain.base.exception.ApiException
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

val Context.userDataStore by preferencesDataStore(name = "user_data")

class TokenDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @TokenRefreshApi private val tokenApiService: TokenApiService,
    private val tokenStorage: TokenStorage,
): TokenDataSource {

    override suspend fun refreshAccessToken(): Result<Unit> {
        return try {
            val response = tokenApiService.refreshAccessToken()

            if (response.isSuccess && response.result != null) {
                val newAccessToken = response.result.accessToken
                val newRefreshToken = response.result.refreshToken
                tokenStorage.saveTokens(newAccessToken, newRefreshToken)
                Result.success(Unit)
            } else {
                Result.failure(ApiException.TokenExpired("토큰이 만료되었습니다."))
            }
        } catch (e: Exception) {
            Result.failure(ApiException.ServerError("서버 오류 발생: ${e.message}"))
        }
    }


    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        tokenStorage.saveTokens(accessToken, refreshToken)
    }

    override suspend fun getAccessToken(): String? {
        return tokenStorage.getAccessToken()
    }

    override suspend fun clearTokens() {
        tokenStorage.clearTokens()
    }

}