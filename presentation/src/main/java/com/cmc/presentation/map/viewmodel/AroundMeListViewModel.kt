package com.cmc.presentation.map.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.cmc.common.constants.PrimitiveValues.Location.DEFAULT_LATITUDE
import com.cmc.common.constants.PrimitiveValues.Location.DEFAULT_LONGITUDE
import com.cmc.domain.feature.location.GetCurrentLocationUseCase
import com.cmc.domain.feature.location.Location
import com.cmc.domain.feature.spot.usecase.GetCategorySpotsWithMapListUseCase
import com.cmc.domain.feature.spot.usecase.ToggleSpotScrapUseCase
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.home.viewmodel.CategorySpotsViewModel.CategorySpotsSideEffect
import com.cmc.presentation.home.viewmodel.CategorySpotsViewModel.CategorySpotsState
import com.cmc.presentation.home.viewmodel.TodaySpotRecommendationViewModel.TodaySpotRecommendSideEffect
import com.cmc.presentation.map.adapter.MapSpotHorizontalMultiImageCardAdapter
import com.cmc.presentation.map.model.MapScreenLocation
import com.cmc.presentation.map.model.SpotWithMapUiModel
import com.cmc.presentation.map.model.toUiModel
import com.cmc.presentation.search.adapter.SpotThumbnailAdapter
import com.cmc.presentation.search.viewmodel.SearchViewModel.SearchStatus
import com.cmc.presentation.spot.model.toListUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    private val getCategorySpotsWithMapListUseCase: GetCategorySpotsWithMapListUseCase,
    private val toggleSpotScrapUseCase: ToggleSpotScrapUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(AroundMeListState())
    val state: StateFlow<AroundMeListState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<AroundMeListSideEffect>()
    val sideEffect: SharedFlow<AroundMeListSideEffect> = _sideEffect.asSharedFlow()

    init {
        observeStateChanges()
    }
    fun initScreenLocation(
        userLatitude: Double,
        userLongitude: Double,
        minLatitude: Double,
        minLongitude: Double,
        maxLatitude: Double,
        maxLongitude: Double,
        withSearch: Boolean
    ) {
        _state.update { it.copy(
            isInitialized = true,
            screenLocation = MapScreenLocation(
                targetLocation = Location(userLatitude, userLongitude),
                minLatitude = minLatitude,
                minLongitude = minLongitude,
                maxLatitude = maxLatitude,
                maxLongitude = maxLongitude,
            ),
            withSearch = withSearch,
        ) }
    }
    private fun fetchSpots(category: SpotCategory) {
        viewModelScope.launch {
            val screenLocation = state.value.screenLocation

            getCategorySpotsWithMapListUseCase.invoke(
                categoryId = category.id,
                userLatitude = screenLocation.targetLocation.latitude,
                userLongitude = screenLocation.targetLocation.longitude,
                minLatitude = screenLocation.minLatitude,
                minLongitude = screenLocation.minLongitude,
                maxLatitude = screenLocation.maxLatitude,
                maxLongitude = screenLocation.maxLongitude,
                withSearch = state.value.withSearch,
                totalCountCallBack = { count ->
                    _state.update { it.copy(itemCount = count) }
                }
            ).cachedIn(viewModelScope)
                .map { it.map { data -> data.toUiModel() } }
                .collectLatest { pagingData ->
                    _state.update { it.copy(
                        isLoading = false,
                        result = pagingData
                    ) }
                }
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
                    val spotIds = spotList.map { it.spotId }
                    val newPagingData = state.value.result.map {
                        if (it.spotId in spotIds) {
                            it.copy(
                                isScraped = it.isScraped.not()
                            )
                        } else {
                            it
                        }
                    }
                    _state.update {
                        it.copy(result = newPagingData)
                    }
                }
        }
    }
    fun onClickHeadButton() { sendSideEffect(AroundMeListSideEffect.NavigateMap) }
    fun onClickBody() { sendSideEffect(AroundMeListSideEffect.NavigateSearchInput) }

    private fun observeStateChanges() {
        viewModelScope.launch {
            var prevCategory: SpotCategory? = null
            _state.map { it.selectedCategoryTab }
                .collectLatest { category ->
                    if (state.value.isInitialized && prevCategory != category) {
                        fetchSpots(category)
                        prevCategory = category
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
        val isInitialized: Boolean = false,
        val itemCount: Int = -1,
        val screenLocation: MapScreenLocation = MapScreenLocation.getDefault(),
        val withSearch: Boolean = false,
        val result: PagingData<SpotWithMapUiModel> = PagingData.empty(),
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