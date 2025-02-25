package com.cmc.domain.feature.spot.usecase

import com.cmc.domain.feature.spot.model.SpotScrapResponse
import com.cmc.domain.feature.spot.repository.SpotRepository
import javax.inject.Inject

class ToggleSpotScrapUseCase @Inject constructor(
    private val spotRepository: SpotRepository
) {
    suspend operator fun invoke(spotIds: List<Int>): Result<List<SpotScrapResponse>> {
        return spotRepository.toggleSpotScrap(spotIds)
    }
}