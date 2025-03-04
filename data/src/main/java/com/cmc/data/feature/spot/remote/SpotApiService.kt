package com.cmc.data.feature.spot.remote

import com.cmc.data.base.ApiResponse
import com.cmc.data.feature.spot.model.CategorySpotsResponseDto
import com.cmc.data.feature.spot.model.CreateReviewRequest
import com.cmc.data.feature.spot.model.CreateReviewResponseDto
import com.cmc.data.feature.spot.model.CreateSpotResponseDto
import com.cmc.data.feature.spot.model.EditSpotRequestBody
import com.cmc.data.feature.spot.model.EditSpotResponseDto
import com.cmc.data.feature.spot.model.MapListResponseDto
import com.cmc.data.feature.spot.model.MySpotsResponseDto
import com.cmc.data.feature.spot.model.ReviewResponseDto
import com.cmc.data.feature.spot.model.SpotPreviewResponseDto
import com.cmc.data.feature.spot.model.SearchSpotsResponseDto
import com.cmc.data.feature.spot.model.SpotDetailResponseDto
import com.cmc.data.feature.spot.model.SpotScrapResponseDto
import com.cmc.data.feature.spot.model.SpotWithMapResponseDto
import com.cmc.data.feature.spot.model.TodayRecommendedSpotResponseDto
import com.cmc.data.feature.spot.model.TodayRecommendedSpotWithHomeResponseDto
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

    @GET("spot/my-spots")
    suspend fun getMySpots(
    ): ApiResponse<MySpotsResponseDto>

    @GET("spot/today/list")
    suspend fun getTodayRecommendedSpots(
        @Query("userLatitude") userLatitude: Double,
        @Query("userLongitude") userLongitude: Double,
    ): ApiResponse<List<TodayRecommendedSpotResponseDto>>

    @GET("spot/today")
    suspend fun getHomeTodayRecommendedSpots(): ApiResponse<List<TodayRecommendedSpotWithHomeResponseDto>>

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

    @GET("map/in-bound/map")
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

    @GET("map/in-bound/list")
    suspend fun getCategorySpotsWithMapList(
        @Query("categoryId") categoryId: Int?,
        @Query("minLatitude") minLatitude: Double,
        @Query("minLongitude") minLongitude: Double,
        @Query("maxLatitude") maxLatitude: Double,
        @Query("maxLongitude") maxLongitude: Double,
        @Query("userLatitude") userLatitude: Double,
        @Query("userLongitude") userLongitude: Double,
        @Query("withSearch") withSearch: Boolean,
        @Query("page") page: Int,
    ): ApiResponse<MapListResponseDto>

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

    @GET("map/density")
    suspend fun checkSpotRegistration(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
    ): ApiResponse<Any>


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

    @PATCH("spot/{spot_id}")
    suspend fun editSpot(
        @Path("spot_id") spotId: Int,
        @Body spotRequestBody: EditSpotRequestBody,
    ): ApiResponse<EditSpotResponseDto>

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
    ): ApiResponse<CreateReviewResponseDto>

    @DELETE("review/delete/{review_id}")
    suspend fun deleteReview(
        @Path("review_id") reviewId: Int
    ): ApiResponse<Unit>


    @GET("scrap")
    suspend fun getScrapSpots(
    ): ApiResponse<List<SpotPreviewResponseDto>>
}