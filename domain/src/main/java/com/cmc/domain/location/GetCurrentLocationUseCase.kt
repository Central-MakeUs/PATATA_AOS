package com.cmc.domain.location

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
)  {
    suspend operator fun invoke(): Result<Location> {
        return locationRepository.getCurrentLocation()
    }
}