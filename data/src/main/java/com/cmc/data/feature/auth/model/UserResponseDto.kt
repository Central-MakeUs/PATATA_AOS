package com.cmc.data.feature.auth.model

import com.cmc.domain.feature.auth.model.AuthResponse
import com.google.gson.annotations.SerializedName

data class UserResponseDto(
    @SerializedName("nickName")
    var nickName: String? = null,
    @SerializedName("email")
    val email: String,
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
)

fun UserResponseDto.toDomain(): AuthResponse {
    return AuthResponse(
        nickName = nickName,
        email = email,
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
}
