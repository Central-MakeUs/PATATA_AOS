package com.cmc.presentation.report.view

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmc.common.base.BaseFragment
import com.cmc.common.constants.NavigationKeys
import com.cmc.domain.model.ReportType
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentReportBinding
import com.cmc.presentation.report.adapter.ReportReasonAdapter
import com.cmc.presentation.report.model.ReportItem
import com.cmc.presentation.report.viewmodel.ReportViewModel
import com.cmc.presentation.report.viewmodel.ReportViewModel.ReportState
import com.cmc.presentation.report.viewmodel.ReportViewModel.ReportSideEffect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReportFragment: BaseFragment<FragmentReportBinding>(R.layout.fragment_report) {

    private val viewModel: ReportViewModel by viewModels()

    private lateinit var reportReasonAdapter: ReportReasonAdapter

    override fun initObserving() {
        repeatWhenUiStarted {
            launch { viewModel.state.collect { state -> updateUI(state)} }
            launch { viewModel.sideEffect.collectLatest { effect -> handleSideEffect(effect)} }
        }
    }
    override fun initView() {
        val type = arguments?.getInt(NavigationKeys.Report.ARGUMENT_REPORT_TYPE) ?: 0
        val targetId = arguments?.getInt(NavigationKeys.Report.ARGUMENT_REPORT_TARGET_ID) ?: 0

        val reportType = ReportType.fromType(type)
        viewModel.initState(reportType, targetId)

        setAppBar(reportType)
        setReportReasonRecyclerView(reportType)
        setButton()
    }
    private fun updateUI(state: ReportState) {
        binding.layoutReportButton.isEnabled = state.isReportEnabled
    }
    private fun handleSideEffect(effect: ReportSideEffect) {
        when (effect) {
            is ReportSideEffect.Finish -> { finish() }
        }
    }

    private fun setAppBar(reportType: ReportType) {
        binding.reportAppbar.setupAppBar(
            title = getString(ReportItem(reportType).getTitle()),
            onHeadButtonClick = { viewModel.onClickHeaderButton() }
        )
    }
    private fun setReportReasonRecyclerView(reportType: ReportType) {
        val reportReasonList = ReportItem(reportType).getReportReasons()
        reportReasonAdapter = ReportReasonAdapter(
            ReportItem(reportType).getReportReasons()
        ) { (position, str) ->
            viewModel.changedReasonWithDescription(reportReasonList[position].title, str)
        }

        with(binding) {
            rvReportReason.layoutManager = LinearLayoutManager(requireContext())
            rvReportReason.adapter = reportReasonAdapter
        }
    }

    private fun setButton() {
        binding.layoutReportButton.setOnClickListener {
            viewModel.onClickReportButton()
        }
    }
}