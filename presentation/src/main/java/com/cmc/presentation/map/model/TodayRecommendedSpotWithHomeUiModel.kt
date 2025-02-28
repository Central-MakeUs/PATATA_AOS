package com.cmc.presentation.map.model

import com.cmc.domain.feature.spot.model.TodayRecommendedSpotWithHome

data class TodayRecommendedSpotWithHomeUiModel(
    val spotId: Int,
    val spotName: String,
    val address: String,
    val addressDetail: String?,
    val categoryId: Int,
    val imageUrl: String,
    val isScraped: Boolean,
    val tags: List<String>,
)

fun TodayRecommendedSpotWithHome.toUiModel(): TodayRecommendedSpotWithHomeUiModel {
    return TodayRecommendedSpotWithHomeUiModel(
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

fun List<TodayRecommendedSpotWithHome>.toListUiModel(): List<TodayRecommendedSpotWithHomeUiModel> {
    return this.map { it.toUiModel() }
}
