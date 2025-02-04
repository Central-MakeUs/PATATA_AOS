package com.cmc.data.feature.auth.repository

import android.content.Context
import com.cmc.data.feature.auth.model.LoginRequest
import com.cmc.data.feature.auth.model.toDomain
import com.cmc.data.feature.auth.remote.AuthApiService
import com.cmc.data.base.apiRequestCatching
import com.cmc.data.base.asFlow
import com.cmc.data.preferences.TokenPreferences
import com.cmc.data.preferences.UserPreferences
import com.cmc.domain.feature.auth.model.AuthResponse
import com.cmc.domain.feature.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


internal class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenPreferences: TokenPreferences,
    private val userPreferences: UserPreferences,
): AuthRepository {

    override suspend fun login(idToken: String): Flow<Result<AuthResponse>> {
        return apiRequestCatching(
            apiCall = { authApiService.googleLogin(LoginRequest(idToken))},
            transform = { it.toDomain() },
            successCallBack = { result ->
                saveTokens(result.accessToken, result.refreshToken)
            }
        ).asFlow()
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

    override suspend fun saveUserId(userId: String) {
        userPreferences.saveUserId(userId)
    }

    override suspend fun getUserId(): String? {
        return userPreferences.getUserId()
    }
}