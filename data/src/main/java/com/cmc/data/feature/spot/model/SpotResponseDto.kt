package com.cmc.data.feature.spot.model

import com.cmc.domain.model.Spot
import com.google.gson.annotations.SerializedName

data class SpotResponseDto(
    @SerializedName("spotId") val spotId: Int,
    @SerializedName("spotName") val spotName: String,
    @SerializedName("spotAddress") val address: String,
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("tags") val tags: List<String>,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
)

fun SpotResponseDto.toDomain(): Spot {
    return Spot(
        spotId = this.spotId,
        spotName = this.spotName,
        address = this.address,
        categoryId = this.categoryId,
        tags = this.tags,
        latitude = latitude,
        longitude = longitude,
    )
}
