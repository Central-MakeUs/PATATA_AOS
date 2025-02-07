package com.cmc.domain.feature.spot.model

data class SpotWithStatus(
    val spot: Spot,
    val image: String?,
    val reviewCount: Int,
    val scrapCount: Int,
    val isScraped: Boolean,
)
