package com.cmc.data.feature.spot.remote

import com.cmc.data.base.ApiResponse
import com.cmc.data.feature.spot.model.CreateReviewRequest
import com.cmc.data.feature.spot.model.ReviewResponseDto
import com.cmc.data.feature.spot.model.SpotDetailResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface SpotApiService {

    @GET("spot/{spotId}")
    suspend fun getSpotDetail(
        @Path("spotId") spotId: Int
    ): ApiResponse<SpotDetailResponseDto>

    @DELETE("spot/{spotId}")
    suspend fun deleteSpot(
        @Path("spotId") spotId: Int
    ): ApiResponse<Unit>


    @PATCH("scrap/{spotId}")
    suspend fun toggleSpotScrap(
        @Path("spotId") spotId: Int
    ): ApiResponse<Unit>


    @POST("review/create")
    suspend fun createReview(
        @Body request: CreateReviewRequest
    ): ApiResponse<ReviewResponseDto>

    @DELETE("review/{reviewId")
    suspend fun deleteReview(
        @Path("reviewId") reviewId: Int
    ): ApiResponse<Unit>

}