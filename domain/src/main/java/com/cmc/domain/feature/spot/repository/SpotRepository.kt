package com.cmc.domain.feature.spot.repository

import com.cmc.domain.feature.spot.model.Review
import com.cmc.domain.feature.spot.model.SpotDetail

interface SpotRepository {

    suspend fun getSpotDetail(spotId: Int): Result<SpotDetail>

    suspend fun deleteSpot(spotId: Int): Result<Unit>

    suspend fun toggleSpotScrap(spotId: Int): Result<Unit>

    suspend fun createReview(spotId: Int, reviewText: String): Result<Review>

    suspend fun deleteReview(reviewId: Int): Result<Unit>

}