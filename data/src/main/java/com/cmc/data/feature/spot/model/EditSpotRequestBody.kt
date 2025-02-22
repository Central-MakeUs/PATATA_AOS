package com.cmc.data.feature.spot.model

import com.google.gson.annotations.SerializedName

data class EditSpotRequestBody(
    @SerializedName("spotName")
    val spotName: String,
    @SerializedName("spotAddress")
    val spotAddress: String,
    @SerializedName("spotAddressDetail")
    val spotAddressDetail: String?,
    @SerializedName("spotDescription")
    val spotDesc: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("tags")
    val tags: List<String>?,
)
