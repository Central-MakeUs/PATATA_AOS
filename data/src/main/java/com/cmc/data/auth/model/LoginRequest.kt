package com.cmc.data.auth.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("idToken")
    val idToken: String,
)
