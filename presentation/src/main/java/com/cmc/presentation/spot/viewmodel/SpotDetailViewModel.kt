package com.cmc.presentation.spot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.feature.spot.usecase.CreateReviewUseCase
import com.cmc.domain.feature.spot.usecase.DeleteReviewUseCase
import com.cmc.domain.feature.spot.usecase.GetSpotDetailUseCase
import com.cmc.domain.feature.spot.usecase.ToggleSpotScrapUseCase
import com.cmc.presentation.spot.model.SpotDetailUiModel
import com.cmc.presentation.spot.model.toUiModel
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
class SpotDetailViewModel @Inject constructor(
    private val getSpotDetailUseCase: GetSpotDetailUseCase,
    private val toggleSpotScrapUseCase: ToggleSpotScrapUseCase,
    private val createReviewUseCase: CreateReviewUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
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
        data class ShowSnackBar(val message: String) : SpotDetailSideEffect()
        data class ShowBottomSheet(val spotIsMine: Boolean): SpotDetailSideEffect()
        data object Finish: SpotDetailSideEffect()
    }
}