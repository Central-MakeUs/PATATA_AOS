package com.cmc.domain.feature.spot.repository

import com.cmc.domain.feature.spot.base.PaginatedResponse
import com.cmc.domain.feature.spot.model.CreateReviewResponse
import com.cmc.domain.feature.spot.model.Review
import com.cmc.domain.feature.spot.model.SpotPreview
import com.cmc.domain.feature.spot.model.SpotDetail
import com.cmc.domain.feature.spot.model.SpotScrapResponse
import com.cmc.domain.feature.spot.model.SpotWithDistance
import com.cmc.domain.feature.spot.model.SpotWithMap
import com.cmc.domain.feature.spot.model.SpotWithStatus
import com.cmc.domain.feature.spot.model.TodayRecommendedSpot
import com.cmc.domain.feature.spot.model.TodayRecommendedSpotWithHome
import com.cmc.domain.model.ImageMetadata

interface SpotRepository {

    suspend fun getMySpots(): Result<List<SpotPreview>>

    suspend fun getTodayRecommendedSpots(
        latitude: Double,
        longitude: Double,
    ): Result<List<TodayRecommendedSpot>>

    suspend fun getHomeTodayRecommendedSpots(): Result<List<TodayRecommendedSpotWithHome>>

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

    suspend fun getCategorySpotsWithMap(
        categoryId: Int,
        minLatitude: Double,
        minLongitude: Double,
        maxLatitude: Double,
        maxLongitude: Double,
        userLatitude: Double,
        userLongitude: Double,
        withSearch: Boolean,
    ): Result<List<SpotWithMap>>

    suspend fun getSearchSpotsWithMap(
        keyword: String,
        minLatitude: Double?,
        minLongitude: Double?,
        maxLatitude: Double?,
        maxLongitude: Double?,
        userLatitude: Double,
        userLongitude: Double,
    ): Result<SpotWithMap>

    suspend fun checkSpotRegistration(latitude: Double, longitude: Double): Result<String>

    suspend fun getSpotDetail(spotId: Int): Result<SpotDetail>

    suspend fun createSpot(
        spotName: String,
        spotDesc: String,
        spotAddress: String,
        spotAddressDetail: String?,
        latitude: Double,
        longitude: Double,
        categoryId: Int,
        tags: List<String>?,
        images: List<ImageMetadata>,
    ): Result<Unit>

    suspend fun editSpot(
        spotId: Int,
        spotName: String,
        spotDesc: String,
        spotAddress: String,
        spotAddressDetail: String?,
        latitude: Double,
        longitude: Double,
        categoryId: Int,
        tags: List<String>?,
    ): Result<Unit>

    suspend fun deleteSpot(spotId: Int): Result<Unit>

    suspend fun toggleSpotScrap(spotIds: List<Int>): Result<List<SpotScrapResponse>>

    suspend fun createReview(spotId: Int, reviewText: String): Result<CreateReviewResponse>

    suspend fun deleteReview(reviewId: Int): Result<Unit>

    suspend fun getScrapSpots(): Result<List<SpotPreview>>
}