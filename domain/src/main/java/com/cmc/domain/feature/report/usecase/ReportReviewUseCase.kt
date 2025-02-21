package com.cmc.domain.feature.report.usecase

import com.cmc.domain.feature.report.repository.ReportRepository
import javax.inject.Inject

class ReportReviewUseCase @Inject constructor(
    private val reportRepository: ReportRepository
)  {
    suspend operator fun invoke(reviewId: Int, reason: String, description: String?): Result<String> {
        return reportRepository.reportReview(reviewId, reason, description)
    }
}