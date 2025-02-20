package com.cmc.domain.feature.spot.usecase

import com.cmc.domain.feature.spot.model.SpotPreview
import com.cmc.domain.feature.spot.repository.SpotRepository
import javax.inject.Inject

class GetMySpotsUseCase @Inject constructor(
    private val spotRepository: SpotRepository
) {
    suspend operator fun invoke(): Result<List<SpotPreview>> {
        return spotRepository.getMySpots()
    }
}