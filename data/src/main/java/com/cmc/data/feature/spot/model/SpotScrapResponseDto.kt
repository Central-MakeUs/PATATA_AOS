package com.cmc.data.feature.spot.model

import com.google.gson.annotations.SerializedName

data class SpotScrapResponseDto(
    @SerializedName("spotId")
    val spotId: Int,
    @SerializedName("totalScraps")
    val totalScraps: Int,
    @SerializedName("message")
    val message: String,
)
