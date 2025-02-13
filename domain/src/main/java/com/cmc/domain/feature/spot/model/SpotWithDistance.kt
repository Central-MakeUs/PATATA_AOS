package com.cmc.domain.feature.spot.model

data class SpotWithDistance(
    val spotId: Int,
    val spotName: String,
    val image: String,
    val reviewCount: Int,
    val scrapCount: Int,
    val isScraped: Boolean,
    val distance: Double,
)
