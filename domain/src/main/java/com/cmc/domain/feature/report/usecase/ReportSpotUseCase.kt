package com.cmc.domain.feature.report.usecase

import com.cmc.domain.feature.report.repository.ReportRepository
import javax.inject.Inject

class ReportSpotUseCase @Inject constructor(
    private val reportRepository: ReportRepository
)  {
    suspend operator fun invoke(spotId: Int, reason: String, description: String?): Result<String> {
        return reportRepository.reportSpot(spotId, reason, description)
    }
}