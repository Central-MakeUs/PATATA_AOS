package com.cmc.domain.feature.report.repository

interface ReportRepository {

    suspend fun reportSpot(spotId: Int, reason: String, description: String?): Result<String>

    suspend fun reportReview(reviewId: Int, reason: String, description: String?): Result<String>

    suspend fun reportUser(memberId: Int, reason: String, description: String?): Result<String>

}