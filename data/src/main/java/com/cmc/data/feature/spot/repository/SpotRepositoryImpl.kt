package com.cmc.data.feature.spot.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.cmc.data.base.apiRequestCatching
import com.cmc.data.feature.spot.CategorySpotPagingSource
import com.cmc.data.feature.spot.model.CreateReviewRequest
import com.cmc.data.feature.spot.model.toDomain
import com.cmc.data.feature.spot.remote.SpotApiService
import com.cmc.domain.feature.spot.base.PaginatedResponse
import com.cmc.domain.feature.spot.model.Review
import com.cmc.domain.feature.spot.model.SpotDetail
import com.cmc.domain.feature.spot.model.SpotWithStatus
import com.cmc.domain.feature.spot.repository.SpotRepository
import javax.inject.Inject

class SpotRepositoryImpl @Inject constructor(
    private val spotApiService: SpotApiService,
): SpotRepository {

    override suspend fun getCategorySpots(
        categoryId: Int,
        latitude: Double,
        longitude: Double,
        sortBy: String
    ): PaginatedResponse<SpotWithStatus> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CategorySpotPagingSource(spotApiService, categoryId, latitude, longitude, sortBy)
            }
        ).flow
    }

    override suspend fun getSpotDetail(spotId: Int): Result<SpotDetail> {
        return apiRequestCatching(
            apiCall = { spotApiService.getSpotDetail(spotId) },
            transform = { it.toDomain() }
        )
    }

    override suspend fun deleteSpot(spotId: Int): Result<Unit> {
        return apiRequestCatching(
            apiCall = { spotApiService.deleteSpot(spotId) },
            transform = {}
        )
    }

    override suspend fun toggleSpotScrap(spotId: Int): Result<Unit> {
        return apiRequestCatching(
            apiCall = { spotApiService.toggleSpotScrap(spotId) },
            transform = {}
        )
    }

    override suspend fun createReview(spotId: Int, reviewText: String): Result<Review> {
        return apiRequestCatching(
            apiCall = { spotApiService.createReview(
                CreateReviewRequest(spotId, reviewText)
            )},
            transform = { it.toDomain() }
        )
    }

    override suspend fun deleteReview(reviewId: Int): Result<Unit> {
        return apiRequestCatching(
            apiCall = { spotApiService.deleteReview(reviewId) },
            transform = {}
        )
    }

}