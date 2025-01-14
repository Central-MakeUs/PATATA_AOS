package com.cmc.data.auth.mapper

import com.cmc.data.auth.model.ApiResponse
import com.cmc.data.auth.model.UserResponseDto
import com.cmc.domain.auth.model.User

internal fun ApiResponse<UserResponseDto>.toDomain(): User {
    val response = this.result ?: throw NullPointerException("data is null")
    return response.run {
        User(
            nickName = nickName,
            email = email,
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }
}