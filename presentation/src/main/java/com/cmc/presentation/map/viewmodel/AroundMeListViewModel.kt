package com.cmc.presentation.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.common.constants.PrimitiveValues.Location.DEFAULT_LATITUDE
import com.cmc.common.constants.PrimitiveValues.Location.DEFAULT_LONGITUDE
import com.cmc.domain.feature.location.GetCurrentLocationUseCase
import com.cmc.domain.feature.location.Location
import com.cmc.domain.feature.spot.usecase.ToggleSpotScrapUseCase
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.home.viewmodel.CategorySpotsViewModel.CategorySpotsSideEffect
import com.cmc.presentation.home.viewmodel.CategorySpotsViewModel.CategorySpotsState
import com.cmc.presentation.home.viewmodel.TodaySpotRecommendationViewModel.TodaySpotRecommendSideEffect
import com.cmc.presentation.map.model.SpotWithMapUiModel
import com.cmc.presentation.spot.model.toListUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AroundMeListViewModel @Inject constructor(
    private val toggleSpotScrapUseCase: ToggleSpotScrapUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(AroundMeListState())
    val state: StateFlow<AroundMeListState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<AroundMeListSideEffect>()
    val sideEffect: SharedFlow<AroundMeListSideEffect> = _sideEffect.asSharedFlow()

    init {
        observeStateChanges()
    }

    fun initSpots(spots: List<SpotWithMapUiModel>) {
        _state.update {
            it.copy(isLoading = false, originalSpots = spots, spots = spots)
        }
    }

    fun onClickCategoryTab(category: SpotCategory) {
        _state.update {
            it.copy(selectedCategoryTab = category)
        }
    }
    fun onClickSpotImage(spotId: Int) {
        sendSideEffect(AroundMeListSideEffect.NavigateSpotDetail(spotId))
    }
    fun onClickSpotScrapButton(spotId: Int) {
        viewModelScope.launch {
            toggleSpotScrapUseCase.invoke(listOf(spotId))
                .onSuccess { spotList ->
                    val spotIds = spotList.toListUiModel().map { it.spotId }
                    val newOriginalData = state.value.spots.map {
                        it.copy(isScraped = if (it.spotId in spotIds) it.isScraped.not() else it.isScraped)
                    }
                    val newData = state.value.spots.map {
                        it.copy(isScraped = if (it.spotId in spotIds) it.isScraped.not() else it.isScraped)
                    }
                    _state.update {
                        it.copy(
                            originalSpots = newOriginalData,
                            spots = newData,
                        )
                    }
                }
        }
    }
    fun onClickHeadButton() { sendSideEffect(AroundMeListSideEffect.NavigateMap) }
    fun onClickBody() { sendSideEffect(AroundMeListSideEffect.NavigateSearchInput) }

    private fun observeStateChanges() {
        viewModelScope.launch {
            _state.map { it.selectedCategoryTab }.distinctUntilChanged()
                .collectLatest { category ->
                    _state.update {
                        it.copy(
                            spots = state.value.originalSpots.filter { spot ->
                                if (category.id == SpotCategory.ALL.id) true
                                else spot.categoryId == category.id
                            }
                        )
                    }
                }
        }
    }

    private fun sendSideEffect(effect: AroundMeListSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class AroundMeListState(
        val isLoading: Boolean = true,
        val originalSpots: List<SpotWithMapUiModel> = emptyList(),
        val spots: List<SpotWithMapUiModel> = emptyList(),
        var selectedCategoryTab: SpotCategory = SpotCategory.ALL,
    )
    sealed class AroundMeListSideEffect {
        data object NavigateSearchInput: AroundMeListSideEffect()
        data object NavigateMap: AroundMeListSideEffect()
        data class NavigateSpotDetail(val spotId: Int): AroundMeListSideEffect()
    }
}