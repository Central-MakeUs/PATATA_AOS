package com.cmc.domain.auth.model

data class User(
    val nickName: String,
    val email: String,
    val accessToken: String,
    val refreshToken: String,
)
