package com.cmc.presentation.spot.model

import com.cmc.domain.feature.spot.model.ScrapSpot

data class ScrapSpotUiModel(
    val spotId : Int,
    val spotName: String,
    val representativeImageUrl: String,
    val isSelected: Boolean = false,
)

fun ScrapSpot.toUiModel(): ScrapSpotUiModel {
    return ScrapSpotUiModel(
        spotId = spotId,
        spotName = spotName,
        representativeImageUrl = representativeImageUrl,
    )
}

fun List<ScrapSpot>.toListUiModel(): List<ScrapSpotUiModel> {
    return this.map { it.toUiModel() }
}