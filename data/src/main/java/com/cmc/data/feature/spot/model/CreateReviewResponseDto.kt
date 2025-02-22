package com.cmc.data.feature.spot.model

import com.cmc.domain.feature.spot.model.CreateReviewResponse
import com.google.gson.annotations.SerializedName

data class CreateReviewResponseDto(
    @SerializedName("reviewId")
    val reviewId: Int,
    @SerializedName("memberName")
    val memberName: String,
    @SerializedName("reviewText")
    val reviewText: String,
    @SerializedName("reviewDate")
    val reviewDate: String,
)

fun CreateReviewResponseDto.toDomain(): CreateReviewResponse {
    return CreateReviewResponse(
        reviewId = reviewId,
        memberName = memberName,
        reviewText = reviewText,
        reviewDate = reviewDate,
    )
}
