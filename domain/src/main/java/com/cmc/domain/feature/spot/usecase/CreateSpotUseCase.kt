package com.cmc.domain.feature.spot.usecase

import com.cmc.domain.feature.spot.repository.SpotRepository
import com.cmc.domain.model.ImageMetadata
import javax.inject.Inject

class CreateSpotUseCase @Inject constructor(
    private val spotRepository: SpotRepository
) {
    suspend operator fun invoke(
        spotName: String,
        spotDesc: String,
        spotAddress: String,
        spotAddressDetail: String?,
        latitude: Double,
        longitude: Double,
        categoryId: Int,
        tags: List<String>?,
        images: List<ImageMetadata>
    ): Result<Unit> {
        return spotRepository.createSpot(
            spotName = spotName,
            spotDesc = spotDesc,
            spotAddress = spotAddress,
            spotAddressDetail = spotAddressDetail,
            latitude = latitude,
            longitude = longitude,
            categoryId = categoryId,
            tags = tags,
            images = images,
        )
    }
}