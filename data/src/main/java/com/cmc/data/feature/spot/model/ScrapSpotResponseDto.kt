package com.cmc.data.feature.spot.model

import com.cmc.domain.feature.spot.model.ScrapSpot
import com.google.gson.annotations.SerializedName

data class ScrapSpotResponseDto(
    @SerializedName("spotId")
    val spotId : Int,
    @SerializedName("spotName")
    val spotName: String,
    @SerializedName("representativeImageUrl")
    val representativeImageUrl: String
)

fun ScrapSpotResponseDto.toDomain(): ScrapSpot {
    return ScrapSpot(
        spotId = spotId,
        spotName = spotName,
        representativeImageUrl = representativeImageUrl,
    )
}

fun List<ScrapSpotResponseDto>.toListDomain(): List<ScrapSpot> {
    return this.map { it.toDomain() }
}