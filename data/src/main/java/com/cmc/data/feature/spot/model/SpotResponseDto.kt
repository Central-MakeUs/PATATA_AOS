package com.cmc.data.feature.spot.model

import com.cmc.domain.feature.spot.model.Spot
import com.google.gson.annotations.SerializedName

data class SpotResponseDto(
    @SerializedName("spotId") val spotId: Int,
    @SerializedName("spotName") val spotName: String,
    @SerializedName("spotAddress") val address: String,
    @SerializedName("categoryId") val categoryId: Long,
    @SerializedName("tags") val tags: List<String>,
)

fun SpotResponseDto.toDomain(): Spot {
    return Spot(
        spotId = this.spotId,
        spotName = this.spotName,
        address = this.address,
        categoryId = this.categoryId,
        tags = this.tags,
    )
}
