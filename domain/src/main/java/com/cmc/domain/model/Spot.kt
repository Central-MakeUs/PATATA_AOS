package com.cmc.domain.model

data class Spot(
    val spotId: Int,
    val spotName: String,
    val address: String,
    val categoryId: Int,
    val tags: List<String>,
)
