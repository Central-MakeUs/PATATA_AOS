package com.cmc.domain.feature.report.usecase

import com.cmc.domain.feature.report.repository.ReportRepository
import javax.inject.Inject

class ReportUserUseCase @Inject constructor(
    private val reportRepository: ReportRepository
)  {
    suspend operator fun invoke(memberId: Int, reason: String, description: String?): Result<String> {
        return reportRepository.reportUser(memberId, reason, description)
    }
}