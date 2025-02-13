package com.cmc.data.feature.spot.model

import com.cmc.domain.feature.spot.model.SpotWithDistance
import com.google.gson.annotations.SerializedName

data class SpotWithDistanceResponseDto(
    @SerializedName("spotId") val spotId: Int,
    @SerializedName("spotName") val spotName: String,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("spotScraps") val scrapCount: Int,
    @SerializedName("isScraped") val isScraped: Boolean,
    @SerializedName("reviews") val reviewCount: Int,
    @SerializedName("distance") val distance: Double,
)

fun SpotWithDistanceResponseDto.toDomain(): SpotWithDistance {
    return SpotWithDistance(
        spotId = spotId,
        spotName = spotName,
        image = imageUrl,
        reviewCount = reviewCount,
        scrapCount = scrapCount,
        isScraped = isScraped,
        distance = distance,
    )
}