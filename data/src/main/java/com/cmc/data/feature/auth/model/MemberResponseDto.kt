package com.cmc.data.feature.auth.model

import com.cmc.domain.feature.auth.model.Member
import com.google.gson.annotations.SerializedName

data class MemberResponseDto(
    @SerializedName("memberId")
    val memberId: Int,
    @SerializedName("nickName")
    val nickName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("profileImage")
    val profileImage: String,
)

fun MemberResponseDto.toDomain(): Member {
    return Member(
        memberId = memberId,
        nickName = nickName,
        email = email,
        profileImage = profileImage,
    )
}
