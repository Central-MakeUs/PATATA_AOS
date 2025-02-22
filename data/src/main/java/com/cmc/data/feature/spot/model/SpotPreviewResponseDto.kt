package com.cmc.data.feature.spot.model

import com.cmc.domain.feature.spot.model.SpotPreview
import com.google.gson.annotations.SerializedName

data class SpotPreviewResponseDto(
    @SerializedName("spotId")
    val spotId : Int,
    @SerializedName("spotName")
    val spotName: String,
    @SerializedName("representativeImageUrl")
    val representativeImageUrl: String
)

fun SpotPreviewResponseDto.toDomain(): SpotPreview {
    return SpotPreview(
        spotId = spotId,
        spotName = spotName,
        representativeImageUrl = representativeImageUrl,
    )
}

fun List<SpotPreviewResponseDto>.toListDomain(): List<SpotPreview> {
    return this.map { it.toDomain() }
}