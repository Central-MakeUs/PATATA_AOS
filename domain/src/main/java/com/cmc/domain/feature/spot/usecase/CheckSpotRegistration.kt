package com.cmc.domain.feature.spot.usecase

import com.cmc.domain.feature.spot.repository.SpotRepository
import javax.inject.Inject

class CheckSpotRegistration @Inject constructor(
    private val spotRepository: SpotRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): Result<String> {
        return spotRepository.checkSpotRegistration(latitude, longitude)
    }
}