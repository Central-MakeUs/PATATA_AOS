package com.cmc.presentation.spot.model

import com.cmc.domain.feature.spot.model.SpotWithStatus
import com.cmc.presentation.model.SpotUiModel
import com.cmc.presentation.model.toUiModel

data class SpotWithStatusUiModel(
    val spot: SpotUiModel,
    val image: String,
    val reviewCount: Int,
    val scrapCount: Int,
    val isScraped: Boolean,
)

fun SpotWithStatus.toUiModel(): SpotWithStatusUiModel {
    return SpotWithStatusUiModel(
        spot = spot.toUiModel(),
        image = image,
        reviewCount = reviewCount,
        scrapCount = scrapCount,
        isScraped = isScraped,
    )
}
