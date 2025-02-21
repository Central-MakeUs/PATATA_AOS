package com.cmc.domain.feature.spot.model

data class TodayRecommendedSpot(
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
