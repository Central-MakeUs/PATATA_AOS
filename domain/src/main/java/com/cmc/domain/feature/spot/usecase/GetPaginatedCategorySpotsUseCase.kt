package com.cmc.domain.feature.spot.usecase

import com.cmc.domain.feature.spot.base.PaginatedResponse
import com.cmc.domain.feature.spot.model.SpotWithStatus
import com.cmc.domain.feature.spot.repository.SpotRepository
import javax.inject.Inject

class GetPaginatedCategorySpotsUseCase @Inject constructor(
    private val spotRepository: SpotRepository
) {
    suspend operator fun invoke(
        categoryId: Int,
        latitude: Double,
        longitude: Double,
        sortBy: String,
        totalCountCallBack: (Int) -> Unit,
    ): PaginatedResponse<SpotWithStatus> {
        return spotRepository.getPaginatedCategorySpots(
            categoryId = categoryId,
            latitude = latitude,
            longitude = longitude,
            sortBy = sortBy,
            totalCountCallBack = totalCountCallBack
        )
    }
}