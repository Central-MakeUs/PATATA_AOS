package com.cmc.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.cmc.common.constants.PrimitiveValues.Location.DEFAULT_LATITUDE
import com.cmc.common.constants.PrimitiveValues.Location.DEFAULT_LONGITUDE
import com.cmc.domain.base.exception.ApiException
import com.cmc.domain.feature.location.GetCurrentLocationUseCase
import com.cmc.domain.feature.location.Location
import com.cmc.domain.feature.spot.usecase.GetPaginatedCategorySpotsUseCase
import com.cmc.domain.feature.spot.usecase.ToggleSpotScrapUseCase
import com.cmc.domain.model.CategorySortType
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.home.adapter.SpotHorizontalPaginatedCardAdapter
import com.cmc.presentation.spot.model.SpotWithStatusUiModel
import com.cmc.presentation.spot.model.toUiModel
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
class CategorySpotsViewModel @Inject constructor(
    private val getPaginatedCategorySpotsUseCase: GetPaginatedCategorySpotsUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val toggleSpotScrapUseCase: ToggleSpotScrapUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(CategorySpotsState())
    val state: StateFlow<CategorySpotsState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<CategorySpotsSideEffect>()
    val sideEffect: SharedFlow<CategorySpotsSideEffect> = _sideEffect.asSharedFlow()

    init {
        observeStateChanges()
    }

    fun onClickSpotScrapButton(spotId: Int) {
        viewModelScope.launch {
            toggleSpotScrapUseCase.invoke(listOf(spotId))
                .onSuccess {
                    val newPagingData = state.value.categorySpots.map { spot ->
                        if (spot.spot.spotId == spotId) {
                            val isScraped = spot.isScraped.not()
                            spot.copy(
                                isScraped = isScraped,
                                scrapCount = spot.scrapCount + if (isScraped) 1 else - 1
                            )
                        } else {
                            spot
                        }
                    }

                    _state.update { it.copy(categorySpots = newPagingData) }
                }.onFailure { e ->
                    when (e) {
                        is ApiException.NotFound -> {
                            val newPagingData = state.value.categorySpots.filter { spot ->
                                spot.spot.spotId != spotId
                            }
                            _state.update { it.copy(categorySpots = newPagingData) }
                        }
                    }
                }
        }
    }
    fun onClickSpotImage(spotId: Int) {
        sendSideEffect(CategorySpotsSideEffect.NavigateSpotDetail(spotId))
    }
    fun onClickCategoryTab(category: SpotCategory) {
        _state.update {
            it.copy(selectedCategoryTab = category)
        }
    }
    fun onClickCategorySortButton() {
        sendSideEffect(CategorySpotsSideEffect.ShowSortDialog)
    }
    fun onClickSortByDistance() {
        _state.update {
            it.copy(sortType = CategorySortType.DISTANCE)
        }
    }
    fun onClickSortByRecommend() {
        _state.update {
            it.copy(sortType = CategorySortType.RECOMMEND)
        }
    }

    fun setPageAdapterLoadStateListener(adapter: SpotHorizontalPaginatedCardAdapter) {
        adapter.addLoadStateListener { loadState ->
            val isError = loadState.refresh is LoadState.Error || loadState.append is LoadState.Error
            val isLoading = loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading
            val isNothing =
                (loadState.refresh is LoadState.NotLoading || loadState.append is LoadState.NotLoading) && adapter.itemCount == 0

            viewModelScope.launch {
                _state.update {
                    it.copy(
                        isLoading = isLoading,
                        isEmpty = isNothing,
                        isError = isError,
                    )
                }
            }
        }
    }

    private fun observeStateChanges() {
        viewModelScope.launch {
            combine(
                _state.map { it.sortType }.distinctUntilChanged(),
                _state.map { it.selectedCategoryTab }.distinctUntilChanged()
            ) { sortType, category ->
                sortType to category
            }.collectLatest { (sortType, category) ->
                getCurrentLocationUseCase.invoke()
                    .onSuccess { location ->
                        updateCategorySpots(category, location, sortType)
                    }.onFailure { e ->
                        when (e) {
                            is SecurityException -> {
                                val location = Location(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
                                updateCategorySpots(category, location, sortType)
                            }
                        }
                    }
            }
        }
    }

    private suspend fun updateCategorySpots(
        category: SpotCategory,
        location: Location,
        sortType: CategorySortType,
    ) {
        getPaginatedCategorySpotsUseCase.invoke(
            categoryId = category.id,
            latitude = location.latitude,
            longitude = location.longitude,
            sortBy = sortType.name,
            totalCountCallBack = { count ->
                _state.update {
                    it.copy(spotCount = count)
                }
            }
        ).cachedIn(viewModelScope)
            .map { pagingData -> pagingData.map { data -> data.toUiModel() }}
            .collectLatest { data ->
                if (state.value.isInitialized.not()) {
                    _state.update { it.copy(
                        isLoading = true
                    ) }
                    delay(1500)
                }
                _state.update {
                    it.copy(
                        isLoading = false,
                        categorySpots = data
                    )
                }
                _state.update { it.copy(isInitialized = true) }
            }
    }

    private fun sendSideEffect(effect: CategorySpotsSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class CategorySpotsState(
        var isInitialized: Boolean = false,
        var isLoading: Boolean = true,
        var isEmpty: Boolean = true,
        var isError: Boolean = false,
        var categorySpots: PagingData<SpotWithStatusUiModel> = PagingData.empty(),
        var spotCount: Int = -1,
        var selectedCategoryTab: SpotCategory = SpotCategory.ALL,
        var sortType: CategorySortType = CategorySortType.getDefault(),
    )

    sealed class CategorySpotsSideEffect {
        data class NavigateSpotDetail(val spotId: Int): CategorySpotsSideEffect()
        data object ShowSortDialog: CategorySpotsSideEffect()
    }
}