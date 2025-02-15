package com.cmc.domain.feature.spot.repository

import com.cmc.domain.feature.spot.base.PaginatedResponse
import com.cmc.domain.feature.spot.model.Review
import com.cmc.domain.feature.spot.model.SpotDetail
import com.cmc.domain.feature.spot.model.SpotWithDistance
import com.cmc.domain.feature.spot.model.SpotWithStatus
import com.cmc.domain.model.ImageMetadata

interface SpotRepository {

    suspend fun getPaginatedCategorySpots(
        categoryId: Int,
        latitude: Double,
        longitude: Double,
        sortBy: String,
        totalCountCallBack: (Int) -> Unit,
    ): PaginatedResponse<SpotWithStatus>

    suspend fun getCategorySpots(
        categoryId: Int,
        latitude: Double,
        longitude: Double,
        sortBy: String,
    ): Result<List<SpotWithStatus>>

    suspend fun getPaginatedSearchSpots(
        keyword: String,
        latitude: Double,
        longitude: Double,
        sortBy: String,
        totalCountCallBack: (Int) -> Unit,
    ): PaginatedResponse<SpotWithDistance>

    suspend fun getSpotDetail(spotId: Int): Result<SpotDetail>

    suspend fun createSpot(
        spotName: String,
        spotDesc: String?,
        spotAddress: String,
        spotAddressDetail: String?,
        latitude: Double,
        longitude: Double,
        categoryId: Int,
        tags: List<String>?,
        images: List<ImageMetadata>,
    ): Result<Unit>

    suspend fun deleteSpot(spotId: Int): Result<Unit>

    suspend fun toggleSpotScrap(spotId: Int): Result<Unit>

    suspend fun createReview(spotId: Int, reviewText: String): Result<Review>

    suspend fun deleteReview(reviewId: Int): Result<Unit>

}