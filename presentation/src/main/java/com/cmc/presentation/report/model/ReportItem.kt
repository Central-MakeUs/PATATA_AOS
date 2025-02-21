package com.cmc.presentation.report.model

import androidx.annotation.StringRes
import com.cmc.domain.model.ReportType
import com.cmc.presentation.R

data class ReportItem(
    val reportType: ReportType
) {
    @StringRes
    fun getTitle(): Int {
        return when (this.reportType) {
            ReportType.POST -> R.string.title_spot_report
            ReportType.USER -> R.string.title_user_report
        }
    }

    fun getReportReasons(): List<ReportReason> {
        return when (this.reportType) {
            ReportType.POST -> listOf(
                ReportReason("홍보 및 스팸성"),
                ReportReason("욕설 및 협오 표현"),
                ReportReason("개인정보 노출 및 저작권 침해"),
                ReportReason("기타", requiresTextInput = true)
            )
            ReportType.USER -> listOf(
                ReportReason("비매너 사용자에요"),
                ReportReason("게시글을 반복적으로 올려요"),
                ReportReason("적절하지 않은 게시글을 반복적으로 올려요"),
                ReportReason("기타", requiresTextInput = true)
            )
        }
    }
}