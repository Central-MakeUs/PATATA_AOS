package com.cmc.presentation.map.model

import com.cmc.domain.feature.spot.model.SpotWithMap

data class SpotWithMapUiModel(
    val spotId: Int,
    val spotName: String,
    val address: String,
    val addressDetail: String?,
    val latitude: Double,
    val longitude: Double,
    val categoryId: Int,
    val images: List<String>,
    val tags: List<String>,
    val isScraped: Boolean,
    val distance: Double,
)

fun SpotWithMap.toUiModel(): SpotWithMapUiModel {
    return SpotWithMapUiModel(
        spotId = spotId,
        spotName = spotName,
        address = address,
        addressDetail = addressDetail,
        latitude = latitude,
        longitude = longitude,
        categoryId = categoryId,
        images = images,
        tags = tags,
        isScraped = isScraped,
        distance = distance,
    )
}

fun List<SpotWithMap>.toListUiModel(): List<SpotWithMapUiModel> {
    return this.map { it.toUiModel() }
}