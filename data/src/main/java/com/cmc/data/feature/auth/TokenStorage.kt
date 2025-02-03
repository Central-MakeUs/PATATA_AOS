package com.cmc.data.feature.auth

interface TokenStorage {
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun saveAccessToken(accessToken: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clearTokens()
}
