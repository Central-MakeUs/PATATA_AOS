package com.cmc.domain.auth.repository

import com.cmc.domain.auth.model.AuthResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun login(idToken: String): Flow<Result<AuthResponse>>

    suspend fun saveTokens(accessToken: String, refreshToken: String)

    suspend fun getAccessToken(): String?

    suspend fun clearTokens()

    suspend fun saveUserId(userId: String)

    suspend fun getUserId(): String?

}