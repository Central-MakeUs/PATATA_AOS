package com.cmc.domain.feature.spot.model

data class SpotWithMap(
    val spotId: Int,
    val spotName: String,
    val address: String,
    val addressDetail: String?,
    val latitude: Double,
    val longitude: Double,
    val categoryId: Int,
//    val images: List<String>,
    val tags: List<String>,
    val isScraped: Boolean,
    val distance: Double,
)
