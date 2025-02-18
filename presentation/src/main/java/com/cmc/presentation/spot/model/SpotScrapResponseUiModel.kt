package com.cmc.presentation.spot.model

import com.cmc.domain.feature.spot.model.SpotScrapResponse

data class SpotScrapResponseUiModel(
    val spotId: Int,
    val totalScraps: Int,
    val message: String,
)

fun SpotScrapResponse.toUiModel(): SpotScrapResponseUiModel {
    return SpotScrapResponseUiModel(
        spotId = spotId,
        totalScraps = totalScraps,
        message = message,
    )
}

fun List<SpotScrapResponse>.toListUiModel(): List<SpotScrapResponseUiModel> {
    return this.map { it.toUiModel() }
}