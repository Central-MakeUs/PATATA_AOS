package com.cmc.data.feature.spot.model

import com.cmc.domain.feature.spot.model.SpotWithMap
import com.google.gson.annotations.SerializedName

data class SpotWithMapResponseDto(
    @SerializedName("spotId")
    val spotId: Int,
    @SerializedName("spotName")
    val spotName: String,
    @SerializedName("spotAddress")
    val address: String,
    @SerializedName("spotAddressDetail")
    val addressDetail: String?,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("categoryId")
    val categoryId: Int,
    // TODO: 업데이트 되면 반영
//    @SerializedName("images")
//    val images: List<String>,
    @SerializedName("tags")
    val tags: List<String>,
    @SerializedName("isScraped")
    val isScraped: Boolean,
    @SerializedName("distance")
    val distance: Double,
)

fun SpotWithMapResponseDto.toDomain(): SpotWithMap {
    return SpotWithMap(
        spotId = spotId,
        spotName = spotName,
        address = address,
        addressDetail = addressDetail,
        latitude = latitude,
        longitude = longitude,
        categoryId = categoryId,
//        images = images,
        tags = tags,
        isScraped = isScraped,
        distance = distance,
    )
}

fun List<SpotWithMapResponseDto>.toListDomain(): List<SpotWithMap> {
    return this.map { it.toDomain() }
}