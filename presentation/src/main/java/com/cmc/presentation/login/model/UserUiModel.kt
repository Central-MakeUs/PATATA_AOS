package com.cmc.presentation.login.model

import com.cmc.domain.auth.model.User

data class UserUiModel(
    val nickName: String,
    val email: String,
    val accessToken: String,
    val refreshToken: String,
) {
    companion object {
        fun toUiModel(data: User): UserUiModel {
            return data.run {
                UserUiModel(
                    nickName = nickName,
                    email = email,
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                )
            }
        }
    }
}