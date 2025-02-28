package com.cmc.data.feature.spot.model

import com.cmc.domain.feature.spot.model.TodayRecommendedSpotWithHome
import com.google.gson.annotations.SerializedName

data class TodayRecommendedSpotWithHomeResponseDto(
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
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("isScraped")
    val isScraped: Boolean,
    @SerializedName("tags")
    val tags: List<String>,
)

fun TodayRecommendedSpotWithHomeResponseDto.toDomain(): TodayRecommendedSpotWithHome {
    return TodayRecommendedSpotWithHome(
        spotId = spotId,
        spotName = spotName,
        address = address,
        addressDetail = addressDetail,
        categoryId = categoryId,
        imageUrl = imageUrl,
        isScraped = isScraped,
        tags = tags,
    )
}

fun List<TodayRecommendedSpotWithHomeResponseDto>.toListDomain(): List<TodayRecommendedSpotWithHome> {
    return this.map { it.toDomain() }
}