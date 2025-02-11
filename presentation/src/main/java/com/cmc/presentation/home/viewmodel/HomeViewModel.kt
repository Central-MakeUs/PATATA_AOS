package com.cmc.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.common.constants.PrimitiveValues.Location.DEFAULT_LATITUDE
import com.cmc.common.constants.PrimitiveValues.Location.DEFAULT_LONGITUDE
import com.cmc.domain.feature.location.GetCurrentLocationUseCase
import com.cmc.domain.feature.location.Location
import com.cmc.domain.feature.spot.usecase.GetCategorySpotsUseCase
import com.cmc.domain.model.CategorySortType
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.spot.model.SpotWithStatusUiModel
import com.cmc.presentation.spot.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategorySpotsUseCase: GetCategorySpotsUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<HomeState>(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<HomeSideEffect>()
    val sideEffect: SharedFlow<HomeSideEffect> = _sideEffect.asSharedFlow()

    init {
        observeStateChanges()
    }

    fun onClickSpotCategoryButton(category: SpotCategory) {
        _state.update {
            it.copy(selectedCategory = category)
        }
    }

    fun onClickCategoryTab(category: SpotCategory) {
        _state.update {
            it.copy(selectedCategoryTab = category)
        }
    }

    fun onClickTodayRecommendedSpotMoreButton() {
        sendSideEffect(HomeSideEffect.NavigateTodayRecommendedSpot)
    }

    fun onClickSearchBar() {
        sendSideEffect(HomeSideEffect.NavigateSearch)
    }

    fun onClickSpotScrapButton(spotId: Int) {
        viewModelScope.launch {
            // TODO: Scrap Toggle API 호출
            val isSuccess: Boolean = true
            if (isSuccess) {
                _state.update {
                    it.copy(
                        categorySpots = it.categorySpots.map { spot ->
                            if (spot.spot.spotId == spotId) {
                                spot.copy(isScraped = !spot.isScraped)
                            } else {
                                spot
                            }
                        }
                    )
                }
            }
        }
    }

    fun onClickCategoryRecommendMoreButton() {
        viewModelScope.launch {
            _sideEffect.emit(HomeSideEffect.NavigateCategorySpot(_state.value.selectedCategoryTab))
        }
    }

    private fun observeStateChanges() {
        viewModelScope.launch {
            _state.map { it.selectedCategoryTab }.distinctUntilChanged()
                .collectLatest { category ->
                    getCurrentLocationUseCase.invoke()
                        .onSuccess { location ->
                            updateCategorySpots(category, location)
                        }.onFailure { e ->
                            when (e) {
                                is SecurityException -> {
                                    val location = Location(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
                                    updateCategorySpots(category, location)
                                }
                            }
                        }
                }
        }
    }

    private suspend fun updateCategorySpots(category: SpotCategory, location: Location) {
        getCategorySpotsUseCase.invoke(
            categoryId = category.id,
            latitude = location.latitude,
            longitude = location.longitude,
            sortBy = CategorySortType.getDefault().name
        ).onSuccess { dataList ->
            _state.update {
                it.copy(
                    categorySpots = dataList.map { data -> data.toUiModel() }
                )
            }
        }
    }

    private fun sendSideEffect(effect: HomeSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class HomeState(
        var categorySpots: List<SpotWithStatusUiModel> = emptyList(),
        var selectedCategory: SpotCategory? = null,
        var selectedCategoryTab: SpotCategory = SpotCategory.ALL,
    )

    sealed class HomeSideEffect {
        data class NavigateSpotDetail(val spotId: Int): HomeSideEffect()
        data class NavigateCategorySpot(val category: SpotCategory): HomeSideEffect()
        data object NavigateSearch: HomeSideEffect()
        data object NavigateTodayRecommendedSpot: HomeSideEffect()
    }
}