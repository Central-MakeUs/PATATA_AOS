package com.cmc.data.feature.report.remote

import com.cmc.data.base.ApiResponse
import com.cmc.data.feature.report.model.ReportRequest
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ReportApiService {

    @POST("report/spot/{spot_id}")
    suspend fun reportSpot(
        @Path("spot_id") spotId: Int,
        @Body reportRequest: ReportRequest
    ): ApiResponse<String>

    @POST("report/review/{review_id}")
    suspend fun reportReview(
        @Path("review_id") reviewId: Int,
        @Body reportRequest: ReportRequest
    ): ApiResponse<String>

    @POST("report/member/{member_id}")
    suspend fun reportUser(
        @Path("review_id") reviewId: Int,
        @Body reportRequest: ReportRequest
    ): ApiResponse<String>
}