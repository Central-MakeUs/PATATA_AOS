package com.cmc.domain.feature.spot.model

data class SpotScrapResponse(
    val spotId: Int,
    val totalScraps: Int,
    val message: String,
)
