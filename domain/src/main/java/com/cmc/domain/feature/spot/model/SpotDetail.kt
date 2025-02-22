package com.cmc.domain.feature.spot.model

data class SpotDetail(
    val spotId: Int,
    val isAuthor: Boolean,
    val spotName: String,
    val description: String,
    val address: String,
    val addressDetail: String?,
    val latitude: Double,
    val longitude: Double,
    val categoryId: Int,
    val memberId: Int,
    val authorName: String,
    val images: List<String>,
    val tags: List<String>,
    val reviewCount: Int,
    val reviews: List<Review>,
    val isScraped: Boolean,
)
