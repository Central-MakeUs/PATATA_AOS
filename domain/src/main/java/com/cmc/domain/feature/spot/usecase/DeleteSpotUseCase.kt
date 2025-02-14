package com.cmc.domain.feature.spot.usecase

import com.cmc.domain.feature.spot.model.SpotDetail
import com.cmc.domain.feature.spot.repository.SpotRepository
import javax.inject.Inject

class DeleteSpotUseCase @Inject constructor(
    private val spotRepository: SpotRepository
) {
    suspend operator fun invoke(spotId: Int): Result<Unit> {
        return spotRepository.deleteSpot(spotId)
    }
}