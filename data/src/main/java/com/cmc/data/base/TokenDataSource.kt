package com.cmc.data.base


interface TokenDataSource {

    suspend fun refreshAccessToken(): Result<Unit>

    suspend fun saveTokens(accessToken: String, refreshToken: String)

    suspend fun getAccessToken(): String?

    suspend fun clearTokens()

}