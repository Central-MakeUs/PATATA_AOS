package com.cmc.data.feature.spot.model

import com.cmc.domain.feature.spot.model.TodayRecommendedSpot
import com.google.gson.annotations.SerializedName

data class TodayRecommendedSpotResponseDto(
    @SerializedName("spotId")
    val spotId: Int,
    @SerializedName("spotName")
    val spotName: String,
    @SerializedName("spotAddress")
    val address: String,
    @SerializedName("spotAddressDetail")
    val addressDetail: String?,
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("distance")
    val distance: Double,
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("isScraped")
    val isScraped: Boolean,
    @SerializedName("tags")
    val tags: List<String>,
)

fun TodayRecommendedSpotResponseDto.toDomain(): TodayRecommendedSpot {
    return TodayRecommendedSpot(
        spotId = spotId,
        spotName = spotName,
        address = address,
        addressDetail = addressDetail,
        categoryId = categoryId,
        distance = distance,
        images = images,
        isScraped = isScraped,
        tags = tags,
    )
}

fun List<TodayRecommendedSpotResponseDto>.toListDomain(): List<TodayRecommendedSpot> {
    return this.map { it.toDomain() }
}