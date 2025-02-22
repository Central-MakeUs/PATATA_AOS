package com.cmc.presentation.spot.model

import com.cmc.domain.feature.spot.model.SpotPreview

data class SpotPreviewUiModel(
    val spotId : Int,
    val spotName: String,
    val representativeImageUrl: String,
    val isSelected: Boolean = false,
)

fun SpotPreview.toUiModel(): SpotPreviewUiModel {
    return SpotPreviewUiModel(
        spotId = spotId,
        spotName = spotName,
        representativeImageUrl = representativeImageUrl,
    )
}

fun List<SpotPreview>.toListUiModel(): List<SpotPreviewUiModel> {
    return this.map { it.toUiModel() }
}