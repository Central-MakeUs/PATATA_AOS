package com.cmc.data.feature.spot.model

import com.cmc.domain.feature.spot.base.PaginatedData
import com.cmc.domain.feature.spot.model.SpotWithDistance
import com.google.gson.annotations.SerializedName

data class SearchSpotsResponseDto(
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("spots") val spots: List<SpotWithDistanceResponseDto>
)

fun SearchSpotsResponseDto.toDomain(): PaginatedData<SpotWithDistance> {
    return PaginatedData(
        totalCount = totalCount,
        currentPage = currentPage,
        totalPages = totalPages,
        items = spots.map { it.toDomain() }
    )
}