package com.cmc.data.feature.spot.model

import com.cmc.domain.feature.spot.model.Review
import com.google.gson.annotations.SerializedName

data class ReviewResponseDto(
    @SerializedName("reviewId")
    val reviewId: Int,
    @SerializedName("memberName")
    val memberName: String,
    @SerializedName("reviewText")
    val reviewText: String,
    @SerializedName("reviewDate")
    val reviewDate: String,
    @SerializedName("isAuthor")
    val isAuthor: Boolean,
)

fun ReviewResponseDto.toDomain(): Review {
    return Review(
        reviewId = reviewId,
        memberName = memberName,
        reviewText = reviewText,
        reviewDate = reviewDate,
        isAuthor = isAuthor,
    )
}