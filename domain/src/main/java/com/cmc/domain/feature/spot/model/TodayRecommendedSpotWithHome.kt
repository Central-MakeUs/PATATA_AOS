package com.cmc.domain.feature.spot.model

data class TodayRecommendedSpotWithHome(
    val spotId: Int,
    val spotName: String,
    val address: String,
    val addressDetail: String?,
    val categoryId: Int,
    val imageUrl: String,
    val isScraped: Boolean,
    val tags: List<String>,
)
