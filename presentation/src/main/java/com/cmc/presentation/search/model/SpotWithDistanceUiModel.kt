package com.cmc.presentation.search.model

import com.cmc.domain.feature.spot.model.SpotWithDistance

data class SpotWithDistanceUiModel(
    val spotId: Int,
    val spotName: String,
    val image: String,
    val reviewCount: Int,
    val scrapCount: Int,
    val isScraped: Boolean,
    val distance: Double,
)

fun SpotWithDistance.toUiModel(): SpotWithDistanceUiModel {
    return SpotWithDistanceUiModel(
        spotId = spotId,
        spotName = spotName,
        image = image,
        reviewCount = reviewCount,
        scrapCount = scrapCount,
        isScraped = isScraped,
        distance = distance,
    )
}