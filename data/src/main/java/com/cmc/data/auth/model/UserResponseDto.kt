package com.cmc.data.auth.model

import com.cmc.domain.auth.model.User
import com.google.gson.annotations.SerializedName

data class UserResponseDto(
    @SerializedName("nickName")
    val nickName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
)

fun UserResponseDto.toDomain(): User {
    return User(
        nickName = nickName,
        email = email,
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
}
