package com.cmc.domain.feature.spot.model

data class CreateReviewResponse(
    val reviewId: Int,
    val memberName: String,
    val reviewText: String,
    val reviewDate: String,
)