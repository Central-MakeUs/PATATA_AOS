package com.cmc.data.feature.report.repository

import com.cmc.data.base.apiRequestCatching
import com.cmc.data.feature.report.model.ReportRequest
import com.cmc.data.feature.report.remote.ReportApiService
import com.cmc.domain.feature.report.repository.ReportRepository
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val reportApiService: ReportApiService,
): ReportRepository {
    override suspend fun reportSpot(spotId: Int, reason: String, description: String?): Result<String> {
        return apiRequestCatching(
            apiCall = {
                val reportRequest = ReportRequest(reason, description)
                reportApiService.reportSpot(spotId, reportRequest)
            },
            transform = { it }
        )
    }

    override suspend fun reportReview(reviewId: Int, reason: String, description: String?): Result<String> {
        return apiRequestCatching(
            apiCall = {
                val reportRequest = ReportRequest(reason, description)
                reportApiService.reportReview(reviewId, reportRequest)
            },
            transform = { it }
        )
    }

    override suspend fun reportUser(memberId: Int, reason: String, description: String?): Result<String> {
        return apiRequestCatching(
            apiCall = {
                val reportRequest = ReportRequest(reason, description)
                reportApiService.reportUser(memberId, reportRequest)
            },
            transform = { it }
        )
    }
}