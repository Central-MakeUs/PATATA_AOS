package com.cmc.data.feature.spot.model

import com.cmc.domain.feature.spot.model.SpotDetail
import com.google.gson.annotations.SerializedName

data class SpotDetailResponseDto(
    @SerializedName("spotId")
    val spotId: Int,
    @SerializedName("isAuthor")
    val isAuthor: Boolean,
    @SerializedName("spotName")
    val spotName: String,
    @SerializedName("spotDescription")
    val description: String,
    @SerializedName("spotAddress")
    val address: String,
    @SerializedName("spotAddressDetail")
    val addressDetail: String?,
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("memberId")
    val memberId: Int,
    @SerializedName("memberName")
    val authorName: String,
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("tags")
    val tags: List<String>,
    @SerializedName("reviewCount")
    val reviewCount: Int,
    @SerializedName("reviews")
    val reviews: List<ReviewResponseDto>,
    @SerializedName("isScraped")
    val isScraped: Boolean,
)

// Dto to Domain 변환 함수
fun SpotDetailResponseDto.toDomain(): SpotDetail {
    return SpotDetail(
        spotId = spotId,
        isAuthor = isAuthor,
        spotName = spotName,
        description = description,
        address = address,
        addressDetail = addressDetail,
        categoryId = categoryId,
        memberId = memberId,
        authorName = authorName,
        images = images,
        tags = tags,
        reviewCount = reviewCount,
        reviews = reviews.map { it.toDomain() },
        isScraped = isScraped
    )
}