package com.cmc.data.auth.remote

import com.cmc.data.auth.model.ApiResponse
import com.cmc.data.auth.model.LoginRequest
import com.cmc.data.auth.model.UserResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/google/login")
    suspend fun googleLogin(
        @Body request: LoginRequest
    ): ApiResponse<UserResponseDto>

}