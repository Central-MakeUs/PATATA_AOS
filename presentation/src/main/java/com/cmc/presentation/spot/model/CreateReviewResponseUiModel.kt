package com.cmc.presentation.spot.model

import com.cmc.domain.feature.spot.model.CreateReviewResponse

data class CreateReviewResponseUiModel(
    val reviewId: Int,
    val memberName: String,
    val reviewText: String,
    val reviewDate: String,
)

fun CreateReviewResponse.toUiModel(): CreateReviewResponseUiModel {
    return CreateReviewResponseUiModel(
        reviewId = reviewId,
        memberName = memberName,
        reviewText = reviewText,
        reviewDate = reviewDate,
    )
}

fun CreateReviewResponseUiModel.toReviewUiModel(): ReviewUiModel {
    return ReviewUiModel(
        reviewId = reviewId,
        memberName = memberName,
        reviewText = reviewText,
        reviewDate = reviewDate,
        isAuthor = true,
    )
}