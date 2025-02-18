package com.cmc.domain.feature.spot.usecase

import com.cmc.domain.feature.spot.model.SpotWithStatus
import com.cmc.domain.feature.spot.repository.SpotRepository
import javax.inject.Inject

class GetCategorySpotsUseCase @Inject constructor(
    private val spotRepository: SpotRepository
) {
    suspend operator fun invoke(
        categoryId: Int,
        latitude: Double,
        longitude: Double,
        sortBy: String,
    ): Result<List<SpotWithStatus>> {
        return spotRepository.getCategorySpots(
            categoryId = categoryId,
            latitude = latitude,
            longitude = longitude,
            sortBy = sortBy,
        )
    }
}