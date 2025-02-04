package com.cmc.data.feature.token.remote

import com.cmc.data.base.ApiResponse
import com.cmc.domain.feature.auth.model.AuthResponse
import retrofit2.http.Headers
import retrofit2.http.POST

interface TokenApiService {

    @POST("auth/refresh")
    @Headers("Refresh: true")
    suspend fun refreshAccessToken(): ApiResponse<AuthResponse>
}