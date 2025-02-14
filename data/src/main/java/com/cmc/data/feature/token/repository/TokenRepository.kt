package com.cmc.data.feature.token.repository


interface TokenRepository {

    suspend fun refreshAccessToken(): Result<Unit>

    suspend fun saveTokens(accessToken: String, refreshToken: String)

    suspend fun getAccessToken(): String?

    suspend fun clearTokens()

}