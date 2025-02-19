package com.cmc.data.base

import com.google.gson.annotations.SerializedName

data class ErrorResponse<T> (
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("result")
    val result: T? = null
)