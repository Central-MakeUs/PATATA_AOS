package com.cmc.domain.auth.model

data class AuthResponse(
    var nickName: String? = null,
    val email: String,
    val accessToken: String,
    val refreshToken: String,
)
