package com.cmc.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import com.cmc.presentation.search.viewmodel.SearchViewModel.SearchSideEffect
import com.cmc.presentation.spot.model.SpotWithStatusUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TodaySpotRecommendationViewModel @Inject constructor(): ViewModel() {

    private val _state = MutableStateFlow(TodaySpotRecommendationState())
    val state: StateFlow<TodaySpotRecommendationState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SearchSideEffect>()
    val sideEffect: SharedFlow<SearchSideEffect> = _sideEffect.asSharedFlow()

    data class TodaySpotRecommendationState(
        val results: List<SpotWithStatusUiModel> = emptyList(),
        val errorMessage: String? = null,
        val searchStatus: TodaySpotRecommendationStatus = TodaySpotRecommendationStatus.LOADING
    )

    enum class TodaySpotRecommendationStatus {
        LOADING,
        SUCCESS,
        ERROR,
        EMPTY,
    }

}

