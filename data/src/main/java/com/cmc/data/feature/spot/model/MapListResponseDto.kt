package com.cmc.data.feature.spot.model

import com.cmc.domain.feature.spot.base.PaginatedData
import com.cmc.domain.feature.spot.model.SpotWithMap
import com.google.gson.annotations.SerializedName

data class MapListResponseDto(
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("spots") val spots: List<SpotWithMapResponseDto>
)

fun MapListResponseDto.toDomain(): PaginatedData<SpotWithMap> {
    return PaginatedData(
        currentPage = currentPage,
        totalPages = totalPages,
        totalCount = totalCount,
        items = spots.toListDomain(),
    )
}