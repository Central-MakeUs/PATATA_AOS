package com.cmc.data.feature.spot.model

import com.cmc.domain.feature.spot.base.PaginatedData
import com.cmc.domain.feature.spot.model.SpotWithStatus
import com.google.gson.annotations.SerializedName

data class CategorySpotsResponseDto(
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("spots") val spots: List<SpotWithStatusResponseDto>
)

fun CategorySpotsResponseDto.toDomain(): PaginatedData<SpotWithStatus> {
    return PaginatedData(
        totalCount = totalCount,
        currentPage = currentPage,
        totalPages = totalPages,
        items = spots.map { it.toDomain() }
    )
}

fun CategorySpotsResponseDto.toListDomain(): List<SpotWithStatus> {
    return spots
        .take(MAX_RECOMMENDED_CATEGORY_ITEM_COUNT)
        .map { it.toDomain() }
}

const val MAX_RECOMMENDED_CATEGORY_ITEM_COUNT = 3