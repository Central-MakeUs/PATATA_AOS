package com.cmc.data.feature.auth.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("idToken")
    val idToken: String,
)
