package com.cmc.domain.feature.spot.usecase

import com.cmc.domain.feature.spot.model.TodayRecommendedSpot
import com.cmc.domain.feature.spot.repository.SpotRepository
import javax.inject.Inject

class GetTodayRecommendedSpotsUseCase @Inject constructor(
    private val spotRepository: SpotRepository
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
    ): Result<List<TodayRecommendedSpot>> {
        return spotRepository.getTodayRecommendedSpots(latitude, longitude)
    }
}