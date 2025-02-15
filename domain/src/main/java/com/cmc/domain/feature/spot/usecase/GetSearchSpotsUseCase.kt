package com.cmc.domain.feature.spot.usecase

import com.cmc.domain.feature.spot.base.PaginatedResponse
import com.cmc.domain.feature.spot.model.SpotWithDistance
import com.cmc.domain.feature.spot.repository.SpotRepository
import javax.inject.Inject

class GetSearchSpotsUseCase @Inject constructor(
    private val spotRepository: SpotRepository
) {
    suspend operator fun invoke(
        keyword: String,
        latitude: Double,
        longitude: Double,
        sortBy: String,
        totalCountCallBack: (Int) -> Unit,
    ): PaginatedResponse<SpotWithDistance> {
        return spotRepository.getPaginatedSearchSpots(
            keyword = keyword,
            latitude = latitude,
            longitude = longitude,
            sortBy = sortBy,
            totalCountCallBack = totalCountCallBack
        )
    }
}