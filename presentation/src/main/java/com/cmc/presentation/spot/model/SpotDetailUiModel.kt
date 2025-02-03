package com.cmc.presentation.spot.model

import com.cmc.domain.feature.spot.model.SpotDetail

data class SpotDetailUiModel(
    val spotId: Int,
    val isAuthor: Boolean,
    val spotName: String,
    val description: String,
    val address: String,
    val addressDetail: String,
    val categoryId: Int,
    val authorName: String,
    val images: List<String>,
    val tags: List<String>,
    val reviewCount: Int,
    val reviews: List<ReviewUiModel>,
    )

fun SpotDetail.toUiModel(): SpotDetailUiModel {
    return SpotDetailUiModel(
        spotId = this.spotId,
        isAuthor = this.isAuthor,
        spotName = this.spotName,
        description = this.description,
        address = this.address,
        addressDetail = this.addressDetail,
        categoryId = this.categoryId,
        authorName = this.authorName,
        images = this.images,
        tags = this.tags,
        reviewCount = this.reviewCount,
        reviews = this.reviews.map { it.toUiModel() }
    )
}