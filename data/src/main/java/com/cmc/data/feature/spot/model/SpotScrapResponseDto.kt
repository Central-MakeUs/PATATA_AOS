package com.cmc.data.feature.spot.model

import com.cmc.domain.feature.spot.model.SpotScrapResponse
import com.google.gson.annotations.SerializedName

data class SpotScrapResponseDto(
    @SerializedName("spotId")
    val spotId: Int,
    @SerializedName("totalScraps")
    val totalScraps: Int,
    @SerializedName("message")
    val message: String,
)

fun SpotScrapResponseDto.toDomain(): SpotScrapResponse {
    return SpotScrapResponse(
        spotId = spotId,
        totalScraps = totalScraps,
        message = message,
    )
}

fun List<SpotScrapResponseDto>.toListDomain(): List<SpotScrapResponse> {
    return this.map { it.toDomain() }
}