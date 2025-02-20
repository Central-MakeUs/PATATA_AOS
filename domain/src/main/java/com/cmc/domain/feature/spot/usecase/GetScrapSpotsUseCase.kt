package com.cmc.domain.feature.spot.usecase

import com.cmc.domain.feature.spot.model.ScrapSpot
import com.cmc.domain.feature.spot.repository.SpotRepository
import javax.inject.Inject

class GetScrapSpotsUseCase @Inject constructor(
    private val spotRepository: SpotRepository
) {
    suspend operator fun invoke(): Result<List<ScrapSpot>> {
        return spotRepository.getScrapSpots()
    }
}