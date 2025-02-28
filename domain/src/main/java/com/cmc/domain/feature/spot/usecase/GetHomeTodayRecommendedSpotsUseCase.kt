package com.cmc.domain.feature.spot.usecase

import com.cmc.domain.feature.spot.model.TodayRecommendedSpot
import com.cmc.domain.feature.spot.model.TodayRecommendedSpotWithHome
import com.cmc.domain.feature.spot.repository.SpotRepository
import javax.inject.Inject

class GetHomeTodayRecommendedSpotsUseCase @Inject constructor(
    private val spotRepository: SpotRepository
) {
    suspend operator fun invoke(): Result<List<TodayRecommendedSpotWithHome>> {
        return spotRepository.getHomeTodayRecommendedSpots()
    }
}