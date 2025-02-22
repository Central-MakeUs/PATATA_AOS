package com.cmc.presentation.spot.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.feature.spot.usecase.CreateReviewUseCase
import com.cmc.domain.feature.spot.usecase.DeleteReviewUseCase
import com.cmc.domain.feature.spot.usecase.DeleteSpotUseCase
import com.cmc.domain.feature.spot.usecase.GetSpotDetailUseCase
import com.cmc.domain.feature.spot.usecase.ToggleSpotScrapUseCase
import com.cmc.domain.model.ReportType
import com.cmc.presentation.R
import com.cmc.presentation.spot.model.SpotDetailUiModel
import com.cmc.presentation.spot.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
class SpotDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getSpotDetailUseCase: GetSpotDetailUseCase,
    private val toggleSpotScrapUseCase: ToggleSpotScrapUseCase,
    private val createReviewUseCase: CreateReviewUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
    private val deleteSpotUseCase: DeleteSpotUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(SpotDetailState())
    val state: StateFlow<SpotDetailState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SpotDetailSideEffect>()
    val sideEffect: SharedFlow<SpotDetailSideEffect> = _sideEffect.asSharedFlow()


    fun getSpotDetail(spotId: Int) {
        viewModelScope.launch {
            getSpotDetailUseCase.invoke(spotId)
                .onSuccess { result ->
                    _state.update {
                        it.copy(spotDetail = result.toUiModel())
                    }
                }
                .onFailure {
                    sendSideEffect(SpotDetailSideEffect.Finish)
                }
        }
    }


    fun clickFooterButton(spotIsMine: Boolean) {
        sendSideEffect(SpotDetailSideEffect.ShowBottomSheet(spotIsMine))
    }
    fun onClickScrapButton() {
        viewModelScope.launch {
            toggleSpotScrapUseCase.invoke(listOf(state.value.spotDetail.spotId))
                .onSuccess {
                    _state.update {
                        it.copy(
                            spotDetail = it.spotDetail.copy(
                                isScraped = it.spotDetail.isScraped.not()
                            )
                        )
                    }
                }
        }
    }
    fun onClickReviewDelete(reviewId: Int) {
        viewModelScope.launch {
            deleteReviewUseCase.invoke(reviewId)
                .onSuccess {
                    _state.update {
                        it.copy(
                            spotDetail = it.spotDetail.copy(
                                reviewCount = it.spotDetail.reviewCount - 1,
                                reviews = it.spotDetail.reviews.filter { review -> review.reviewId != reviewId }
                            )
                        )
                    }
                }.onFailure {

                }
        }
    }
    fun onClickPostReport() {
        sendSideEffect(
            SpotDetailSideEffect.NavigateReport(
                ReportType.POST.type, state.value.spotDetail.spotId
            )
        )
    }
    fun onClickUserReport() {
        sendSideEffect(
            SpotDetailSideEffect.NavigateReport(
                ReportType.USER.type, state.value.spotDetail.memberId
            )
        )
    }
    fun onClickEditSpot() {
        sendSideEffect(SpotDetailSideEffect.NavigateEditSpot(state.value.spotDetail.spotId))
    }
    fun onClickDeleteSpot() {
        viewModelScope.launch {
            deleteSpotUseCase.invoke(state.value.spotDetail.spotId)
                .onSuccess {
                    sendSideEffect(
                        SpotDetailSideEffect.ShowSnackBar(
                            context.getString(R.string.snackbar_post_delete_complete)
                        )
                    )
                    sendSideEffect(
                        SpotDetailSideEffect.Finish
                    )
                }.onFailure { e ->
                    e.printStackTrace()
                }
        }
    }

    fun submitReviewEditor(text: String) {
        viewModelScope.launch {
            createReviewUseCase.invoke(state.value.spotDetail.spotId,text)
                .onSuccess { review ->
                    _state.update {
                        it.copy(
                            it.spotDetail.copy(
                                reviews = it.spotDetail.reviews + review.toUiModel()
                            )
                        )
                    }
                }.onFailure { e ->
                    e.stackTrace
                }
        }
    }

    private fun sendSideEffect(effect: SpotDetailSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class SpotDetailState(
        var spotDetail: SpotDetailUiModel = SpotDetailUiModel.defaultInstance(),
        val errorMessage: String? = null,
    )

    sealed class SpotDetailSideEffect {
        data object Finish: SpotDetailSideEffect()
        data object ShowAlert : SpotDetailSideEffect()
        data class ShowSnackBar(val message: String) : SpotDetailSideEffect()
        data class ShowBottomSheet(val spotIsMine: Boolean): SpotDetailSideEffect()
        data class NavigateReport(val reportType: Int, val targetId: Int): SpotDetailSideEffect()
        data class NavigateEditSpot(val spotId: Int): SpotDetailSideEffect()
    }
}