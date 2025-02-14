package com.cmc.presentation.login.model

import com.cmc.domain.feature.auth.model.AuthResponse

data class AuthResponseUiModel(
    val nickName: String? = null,
    val email: String,
    val accessToken: String,
    val refreshToken: String,
)

fun AuthResponse.toUiModel(): AuthResponseUiModel {
    return this.run {
        AuthResponseUiModel(
            nickName = nickName,
            email = email,
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }
}