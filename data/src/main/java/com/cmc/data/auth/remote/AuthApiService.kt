package com.cmc.data.auth.remote

import com.cmc.data.base.ApiResponse
import com.cmc.data.auth.model.LoginRequest
import com.cmc.data.auth.model.UserResponseDto
import com.cmc.data.di.GeneralApi
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/google/login")
    @Headers("No-Auth: true")
    suspend fun googleLogin(
        @Body request: LoginRequest
    ): ApiResponse<UserResponseDto>

}