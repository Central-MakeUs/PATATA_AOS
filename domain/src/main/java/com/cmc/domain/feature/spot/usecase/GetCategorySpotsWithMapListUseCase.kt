package com.cmc.domain.feature.spot.usecase

import com.cmc.domain.feature.spot.base.PaginatedResponse
import com.cmc.domain.feature.spot.model.SpotWithMap
import com.cmc.domain.feature.spot.repository.SpotRepository
import javax.inject.Inject

class GetCategorySpotsWithMapListUseCase @Inject constructor(
    private val spotRepository: SpotRepository
) {
    suspend operator fun invoke(
        categoryId: Int,
        minLatitude: Double,
        minLongitude: Double,
        maxLatitude: Double,
        maxLongitude: Double,
        userLatitude: Double,
        userLongitude: Double,
        withSearch: Boolean,
        totalCountCallBack: (Int) -> Unit,
    ): PaginatedResponse<SpotWithMap> {
        return spotRepository.getCategorySpotsWithMapList(
            categoryId = categoryId,
            minLatitude = minLatitude,
            minLongitude = minLongitude,
            maxLatitude = maxLatitude,
            maxLongitude = maxLongitude,
            userLatitude = userLatitude,
            userLongitude = userLongitude,
            withSearch = withSearch,
            totalCountCallBack = totalCountCallBack,
        )
    }
}