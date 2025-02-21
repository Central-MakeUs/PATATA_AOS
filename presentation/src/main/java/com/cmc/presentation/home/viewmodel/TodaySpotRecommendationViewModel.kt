package com.cmc.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.common.constants.PrimitiveValues.Location.DEFAULT_LATITUDE
import com.cmc.common.constants.PrimitiveValues.Location.DEFAULT_LONGITUDE
import com.cmc.domain.feature.location.GetCurrentLocationUseCase
import com.cmc.domain.feature.location.Location
import com.cmc.domain.feature.spot.usecase.GetTodayRecommendedSpotsUseCase
import com.cmc.presentation.map.model.TodayRecommendedSpotUiModel
import com.cmc.presentation.map.model.toListUiModel
import com.cmc.presentation.spot.model.SpotWithStatusUiModel
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
class TodaySpotRecommendationViewModel @Inject constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getTodayRecommendedSpotsUseCase: GetTodayRecommendedSpotsUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(TodaySpotRecommendationState())
    val state: StateFlow<TodaySpotRecommendationState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<TodaySpotRecommendSideEffect>()
    val sideEffect: SharedFlow<TodaySpotRecommendSideEffect> = _sideEffect.asSharedFlow()

    fun refreshHomeScreen() {
        fetchTodayRecommendedSpots()
    }

    private fun fetchTodayRecommendedSpots() {
        viewModelScope.launch {
            getCurrentLocationUseCase.invoke()
                .onSuccess { location ->
                    updateTodayRecommendedSpots(location.latitude, location.longitude)
                }.onFailure { e ->
                    when (e) {
                        is SecurityException -> {
                            val location = Location(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
                            updateTodayRecommendedSpots(location.latitude, location.longitude)
                        }
                    }
                }
        }
    }

    private suspend fun updateTodayRecommendedSpots(latitude: Double, longitude: Double) {
        getTodayRecommendedSpotsUseCase.invoke(latitude, longitude)
            .onSuccess { list ->
                val recommendedSpots = list.toListUiModel()
                _state.update {
                    it.copy(recommendedSpots = recommendedSpots)
                }
            }.onFailure {

            }
    }

    fun onClickSpotImage(spotId: Int) {
        sendSideEffect(TodaySpotRecommendSideEffect.NavigateSpotDetail(spotId))
    }
    fun onClickSpotScrapButton(spotId: Int) {

    }

    private fun sendSideEffect(effect: TodaySpotRecommendSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class TodaySpotRecommendationState(
        val results: List<SpotWithStatusUiModel> = emptyList(),
        val recommendedSpots: List<TodayRecommendedSpotUiModel> = emptyList(),
        val errorMessage: String? = null,
        val searchStatus: TodaySpotRecommendationStatus = TodaySpotRecommendationStatus.LOADING
    )

    enum class TodaySpotRecommendationStatus {
        LOADING,
        SUCCESS,
        ERROR,
        EMPTY,
    }

    sealed class TodaySpotRecommendSideEffect {
        data class NavigateSpotDetail(val spotId: Int): TodaySpotRecommendSideEffect()
    }
}

