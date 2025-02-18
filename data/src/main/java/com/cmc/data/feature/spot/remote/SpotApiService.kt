package com.cmc.data.feature.spot.remote

import com.cmc.data.base.ApiResponse
import com.cmc.data.feature.spot.model.CategorySpotsResponseDto
import com.cmc.data.feature.spot.model.CreateReviewRequest
import com.cmc.data.feature.spot.model.CreateSpotResponseDto
import com.cmc.data.feature.spot.model.ReviewResponseDto
import com.cmc.data.feature.spot.model.SearchSpotsResponseDto
import com.cmc.data.feature.spot.model.SpotDetailResponseDto
import com.cmc.data.feature.spot.model.SpotScrapResponseDto
import com.cmc.data.feature.spot.model.SpotWithMapResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotApiService {

    @GET("spot/category")
    suspend fun getCategorySpots(
        @Query("categoryId") categoryId: Int?,
        @Query("page") page: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("sortBy") sortBy: String
    ): ApiResponse<CategorySpotsResponseDto>

    @GET("spot/search")
    suspend fun getSearchSpots(
        @Query("spotName") spotName: String,
        @Query("page") page: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("sortBy") sortBy: String
    ): ApiResponse<SearchSpotsResponseDto>

    @GET("map/in-bound")
    suspend fun getCategorySpotsWithMap(
        @Query("categoryId") categoryId: Int?,
        @Query("minLatitude") minLatitude: Double,
        @Query("minLongitude") minLongitude: Double,
        @Query("maxLatitude") maxLatitude: Double,
        @Query("maxLongitude") maxLongitude: Double,
        @Query("userLatitude") userLatitude: Double,
        @Query("userLongitude") userLongitude: Double,
        @Query("withSearch") withSearch: Boolean,
    ): ApiResponse<List<SpotWithMapResponseDto>>

    @GET("map/search")
    suspend fun getSearchSpotsWithMap(
        @Query("spotName") spotName: String,
        @Query("minLatitude") minLatitude: Double?,
        @Query("minLongitude") minLongitude: Double?,
        @Query("maxLatitude") maxLatitude: Double?,
        @Query("maxLongitude") maxLongitude: Double?,
        @Query("userLatitude") userLatitude: Double,
        @Query("userLongitude") userLongitude: Double,
    ): ApiResponse<SpotWithMapResponseDto>

    @GET("spot/{spotId}")
    suspend fun getSpotDetail(
        @Path("spotId") spotId: Int
    ): ApiResponse<SpotDetailResponseDto>

    @Multipart
    @POST("spot/create")
    suspend fun createSpot(
        @Part("spotName") spotName: RequestBody,
        @Part("spotDescription") spotDesc: RequestBody?,
        @Part("spotAddress") spotAddress: RequestBody,
        @Part("spotAddressDetail") spotAddressDetail: RequestBody?,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("categoryId") categoryId: RequestBody,
        @Part("tags") tags: RequestBody?,
        @Part images: List<MultipartBody.Part>
    ): ApiResponse<CreateSpotResponseDto>

    @DELETE("spot/{spotId}")
    suspend fun deleteSpot(
        @Path("spotId") spotId: Int
    ): ApiResponse<Unit>

    @PATCH("scrap/toggle")
    suspend fun toggleSpotScrap(
        @Body spotIds: List<Int>
    ): ApiResponse<List<SpotScrapResponseDto>>

    @POST("review/create")
    suspend fun createReview(
        @Body request: CreateReviewRequest
    ): ApiResponse<ReviewResponseDto>

    @DELETE("review/{reviewId")
    suspend fun deleteReview(
        @Path("reviewId") reviewId: Int
    ): ApiResponse<Unit>

}