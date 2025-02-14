package com.cmc.data.feature.auth.model

import com.google.gson.annotations.SerializedName

data class NickNameRequest(
    @SerializedName("nickName")
    val nickName: String
)
