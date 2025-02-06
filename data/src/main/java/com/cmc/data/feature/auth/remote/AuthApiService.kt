package com.cmc.data.feature.auth.remote

import com.cmc.data.base.ApiResponse
import com.cmc.data.feature.auth.model.LoginRequest
import com.cmc.data.feature.auth.model.UserResponseDto
import com.cmc.domain.feature.auth.model.AuthResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/google/login")
    @Headers("No-Auth: true")
    suspend fun googleLogin(
        @Body request: LoginRequest
    ): ApiResponse<UserResponseDto>

    @POST("auth/refresh")
    @Headers("Refresh: true")
    suspend fun refreshAccessToken(): ApiResponse<AuthResponse>
}