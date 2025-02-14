package com.cmc.presentation.model

import com.cmc.domain.model.Spot


data class SpotUiModel(
    val spotId: Int,
    val spotName: String,
    val address: String,
    val categoryId: Int,
    val tags: List<String>,
)

fun Spot.toUiModel(): SpotUiModel {
    return this.run {
        SpotUiModel(
            spotId = spotId,
            spotName = spotName,
            address = address,
            categoryId = categoryId,
            tags = tags,
        )
    }
}