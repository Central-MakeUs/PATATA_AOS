package com.cmc.presentation.map.model

import com.cmc.domain.feature.spot.model.TodayRecommendedSpot

data class TodayRecommendedSpotUiModel(
    val spotId: Int,
    val spotName: String,
    val address: String,
    val addressDetail: String?,
    val categoryId: Int,
    val distance: Double,
    val images: List<String>,
    val isScraped: Boolean,
    val tags: List<String>,
)

fun TodayRecommendedSpot.toUiModel(): TodayRecommendedSpotUiModel {
    return TodayRecommendedSpotUiModel(
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

fun List<TodayRecommendedSpot>.toListUiModel(): List<TodayRecommendedSpotUiModel> {
    return this.map { it.toUiModel() }
}
