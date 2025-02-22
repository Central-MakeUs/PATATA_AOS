package com.cmc.domain.feature.spot.usecase

import com.cmc.domain.feature.spot.repository.SpotRepository
import javax.inject.Inject

class EditSpotUseCase @Inject constructor(
    private val spotRepository: SpotRepository
) {
    suspend operator fun invoke(
        spotId: Int,
        spotName: String,
        spotDesc: String,
        spotAddress: String,
        spotAddressDetail: String?,
        latitude: Double,
        longitude: Double,
        categoryId: Int,
        tags: List<String>?,
    ): Result<Unit> {
        return spotRepository.editSpot(
            spotId = spotId,
            spotName = spotName,
            spotDesc = spotDesc,
            spotAddress = spotAddress,
            spotAddressDetail = spotAddressDetail,
            latitude = latitude,
            longitude = longitude,
            categoryId = categoryId,
            tags = tags
        )
    }
}