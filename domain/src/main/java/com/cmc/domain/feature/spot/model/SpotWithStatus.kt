package com.cmc.domain.feature.spot.model

import com.cmc.domain.model.Spot

data class SpotWithStatus(
    val spot: Spot,
    val image: String,
    val reviewCount: Int,
    val scrapCount: Int,
    val isScraped: Boolean,
)
