package com.cmc.presentation.login.model

import com.cmc.domain.auth.model.User

data class UserUiModel(
    val nickName: String? = null,
    val email: String,
    val accessToken: String,
    val refreshToken: String,
)

fun User.toUiModel(): UserUiModel {
    return this.run {
        UserUiModel(
            nickName = nickName,
            email = email,
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }
}