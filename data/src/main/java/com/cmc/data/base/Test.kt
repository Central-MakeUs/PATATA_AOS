package com.cmc.data.base

import com.google.gson.annotations.SerializedName

data class Test(
    @SerializedName("spotId")
    val spotId: Int,
    @SerializedName("spotName")
    val spotName: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
)
