package com.cmc.domain.feature.auth.model

data class Member(
    val memberId: Int,
    val nickName: String,
    val email: String,
    val profileImage: String?,
)
