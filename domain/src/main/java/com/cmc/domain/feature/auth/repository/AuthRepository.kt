package com.cmc.domain.feature.auth.repository

import com.cmc.domain.feature.auth.model.AuthResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun getOnboardingStatus(): Boolean

    suspend fun setOnboardingStatus(isComplete: Boolean): Result<Boolean>

    suspend fun login(idToken: String): Flow<Result<AuthResponse>>

    suspend fun saveTokens(accessToken: String, refreshToken: String)

    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?

    suspend fun refreshAccessToken(): Result<Unit>

    suspend fun clearTokens()

    suspend fun saveUserId(userId: String)

    suspend fun getUserId(): String?

    suspend fun updateNickName(nickName: String): Result<Unit>

}