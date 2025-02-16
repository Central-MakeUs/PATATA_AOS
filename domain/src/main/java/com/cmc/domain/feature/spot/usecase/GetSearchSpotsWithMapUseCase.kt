package com.cmc.domain.feature.spot.usecase

import com.cmc.domain.feature.spot.model.SpotWithMap
import com.cmc.domain.feature.spot.repository.SpotRepository
import javax.inject.Inject

class GetSearchSpotsWithMapUseCase @Inject constructor(
    private val spotRepository: SpotRepository
) {
    suspend operator fun invoke(
        keyword: String,
        minLatitude: Double,
        minLongitude: Double,
        maxLatitude: Double,
        maxLongitude: Double,
        userLatitude: Double,
        userLongitude: Double
    ): Result<SpotWithMap> {
        return spotRepository.getSearchSpotsWithMap(
            keyword = keyword,
            minLatitude = minLatitude,
            minLongitude = minLongitude,
            maxLatitude = maxLatitude,
            maxLongitude = maxLongitude,
            userLatitude = userLatitude,
            userLongitude = userLongitude,
        )
    }
}