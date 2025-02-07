package com.cmc.domain.feature.spot.model

data class Spot(
    val spotId: Int,
    val spotName: String,
    val address: String,
    val categoryId: Long,
    val tags: List<String>,
)
