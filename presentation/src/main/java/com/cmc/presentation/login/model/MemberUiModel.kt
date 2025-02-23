package com.cmc.presentation.login.model

import com.cmc.domain.feature.auth.model.Member

data class MemberUiModel(
    val memberId: Int,
    val nickName: String,
    val email: String,
    val profileImage: String?,
) {
    companion object {
        fun getDefault(): MemberUiModel {
            return MemberUiModel(
                memberId = -1,
                nickName = "",
                email = "",
                profileImage = ""
            )
        }
    }
}

fun Member.toUiModel(): MemberUiModel {
    return MemberUiModel(
        memberId = memberId,
        nickName = nickName,
        email = email,
        profileImage = profileImage,
    )
}
