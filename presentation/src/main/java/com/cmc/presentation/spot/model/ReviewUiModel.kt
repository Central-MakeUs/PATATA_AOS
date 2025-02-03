package com.cmc.presentation.spot.model

import com.cmc.domain.feature.spot.model.Review

data class ReviewUiModel(
    val reviewId: Int,
    val memberName: String,
    val reviewText: String,
)


fun Review.toUiModel(): ReviewUiModel {
    return ReviewUiModel(
        reviewId = reviewId,
        memberName = memberName,
        reviewText = reviewText
    )
}