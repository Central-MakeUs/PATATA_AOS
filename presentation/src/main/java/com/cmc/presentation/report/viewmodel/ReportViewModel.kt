package com.cmc.presentation.report.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.feature.report.usecase.ReportSpotUseCase
import com.cmc.domain.feature.report.usecase.ReportUserUseCase
import com.cmc.domain.model.ReportType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportUserUseCase: ReportUserUseCase,
    private val reportSpotUseCase: ReportSpotUseCase,
): ViewModel() {

    private val _state: MutableStateFlow<ReportState> = MutableStateFlow(ReportState())
    val state: StateFlow<ReportState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<ReportSideEffect>()
    val sideEffect: SharedFlow<ReportSideEffect> = _sideEffect.asSharedFlow()

    fun initState(type: ReportType, targetId: Int) {
        _state.update {
            it.copy(reportType = type, targetId = targetId)
        }
    }

    fun changedReasonWithDescription(reason: String, description: String?) {
        _state.update {
            it.copy(
                reason = reason,
                description = description,
                isReportEnabled = reason.isNotBlank()
            )
        }
    }
    fun onClickHeaderButton() {
        sendSideEffect(ReportSideEffect.Finish)
    }
    fun onClickReportButton() {
        viewModelScope.launch {
            with(state.value) {
                when (reportType) {
                    ReportType.POST -> reportSpot(targetId, reason, description)
                    else -> reportUser(targetId, reason, description)
                }
            }
        }
    }

    private suspend fun reportSpot(spotId: Int, reason: String, description: String?) {
        reportSpotUseCase.invoke(spotId ,reason, description)
            .onSuccess {

            }.onFailure {

            }
    }

    private suspend fun reportUser(spotId: Int, reason: String, description: String?) {
        reportUserUseCase.invoke(spotId ,reason, description)
            .onSuccess {

            }.onFailure {

            }
    }


    private fun sendSideEffect(effect: ReportSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class ReportState(
        val reportType: ReportType? = null,
        val targetId: Int = 0,
        val reason: String = "",
        val description: String? = null,
        val isReportEnabled: Boolean = false,
    )

    sealed class ReportSideEffect {
        data object Finish: ReportSideEffect()
    }
}