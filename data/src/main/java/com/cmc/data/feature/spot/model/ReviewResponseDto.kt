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
)
// TODO: 생성일자 추가 해주시면 반영하기

fun ReviewResponseDto.toDomain(): Review {
    return Review(
        reviewId = reviewId,
        memberName = memberName,
        reviewText = reviewText,
    )
}