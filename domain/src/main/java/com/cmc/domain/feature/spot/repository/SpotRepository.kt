package com.cmc.domain.feature.spot.repository

import com.cmc.domain.feature.spot.base.PaginatedResponse
import com.cmc.domain.feature.spot.model.Review
import com.cmc.domain.feature.spot.model.SpotDetail
import com.cmc.domain.feature.spot.model.SpotWithStatus

interface SpotRepository {

    suspend fun getCategorySpots(
        categoryId: Int,
        latitude: Double,
        longitude: Double,
        sortBy: String,
    ): PaginatedResponse<SpotWithStatus>

    suspend fun getSpotDetail(spotId: Int): Result<SpotDetail>

    suspend fun deleteSpot(spotId: Int): Result<Unit>

    suspend fun toggleSpotScrap(spotId: Int): Result<Unit>

    suspend fun createReview(spotId: Int, reviewText: String): Result<Review>

    suspend fun deleteReview(reviewId: Int): Result<Unit>

}