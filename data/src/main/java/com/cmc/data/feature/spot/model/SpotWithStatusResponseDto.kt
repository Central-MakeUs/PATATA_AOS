package com.cmc.data.feature.spot.model

import com.cmc.data.feature.spot.SpotWithStatusResponseDtoDeserializer
import com.cmc.domain.feature.spot.model.SpotWithStatus
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

@JsonAdapter(SpotWithStatusResponseDtoDeserializer::class)
data class SpotWithStatusResponseDto(
    val spot: SpotResponseDto,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("reviews") val reviewCount: Int,
    @SerializedName("spotScraps") val scrapCount: Int,
    @SerializedName("isScraped") val isScraped: Boolean,
)

fun SpotWithStatusResponseDto.toDomain(): SpotWithStatus {
    return SpotWithStatus(
        spot = this.spot.toDomain(),
        image = this.imageUrl,
        reviewCount = this.reviewCount,
        scrapCount = this.scrapCount,
        isScraped = this.isScraped,
    )
}