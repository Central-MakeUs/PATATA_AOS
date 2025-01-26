package com.cmc.domain.auth.model

data class User(
    var nickName: String? = null,
    val email: String,
    val accessToken: String,
    val refreshToken: String,
)
