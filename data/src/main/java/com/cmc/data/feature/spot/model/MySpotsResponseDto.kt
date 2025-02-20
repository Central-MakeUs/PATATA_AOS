package com.cmc.data.feature.spot.model

import com.google.gson.annotations.SerializedName

data class MySpotsResponseDto(
    @SerializedName("totalSpots")
    val totalSpots : Int,
    @SerializedName("spots")
    val spots : List<SpotPreviewResponseDto>,
)
