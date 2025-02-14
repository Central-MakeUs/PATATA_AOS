package com.cmc.domain.feature.auth.model

data class AuthResponse(
    var nickName: String? = null,
    val email: String,
    val accessToken: String,
    val refreshToken: String,
)
