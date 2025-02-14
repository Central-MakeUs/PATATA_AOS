package com.cmc.data.feature.spot.model

import com.cmc.domain.feature.spot.model.CreateSpotResponse
import com.google.gson.annotations.SerializedName

data class CreateSpotResponseDto(
    @SerializedName("spotId")
    val spotId: Int,
    @SerializedName("spotName")
    val spotName: String,
)

fun CreateSpotResponseDto.toDomain(): CreateSpotResponse {
    return CreateSpotResponse(
        spotId = spotId,
        spotName = spotName,
    )
}