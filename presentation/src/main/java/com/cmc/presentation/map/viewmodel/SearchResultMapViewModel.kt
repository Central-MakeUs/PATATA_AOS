package com.cmc.presentation.map.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.common.constants.PrimitiveValues.Location.DEFAULT_LATITUDE
import com.cmc.common.constants.PrimitiveValues.Location.DEFAULT_LONGITUDE
import com.cmc.domain.base.exception.ApiException
import com.cmc.domain.feature.location.GetCurrentLocationUseCase
import com.cmc.domain.feature.location.Location
import com.cmc.domain.feature.spot.usecase.GetCategorySpotsWithMapUseCase
import com.cmc.domain.feature.spot.usecase.GetSearchSpotsWithMapUseCase
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.map.model.MapScreenLocation
import com.cmc.presentation.map.model.SpotWithMapUiModel
import com.cmc.presentation.map.model.toListUiModel
import com.cmc.presentation.map.model.toUiModel
import com.cmc.presentation.util.toLocation
import com.naver.maps.geometry.LatLng
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
class SearchResultMapViewModel @Inject constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getSearchSpotsWithMapUseCase: GetSearchSpotsWithMapUseCase,
    private val getCategorySpotsWithMapUseCase: GetCategorySpotsWithMapUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(SearchResultMapState())
    val state: StateFlow<SearchResultMapState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SearchResultMapSideEffect>()
    val sideEffect: SharedFlow<SearchResultMapSideEffect> = _sideEffect.asSharedFlow()

    init {
        observeStateChanges()
    }


    fun initCurrentTargetLocation(location: Location) {
        _state.update { it.copy(currentTargetLocation = location) }
    }
    fun setKeyword(keyword: String) {
        _state.update { it.copy(keyword = keyword) }
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            val result = getCurrentLocationUseCase.invoke()
            result.onSuccess { location ->
                sendSideEffect(SearchResultMapSideEffect.UpdateCurrentLocation(location))
            }.onFailure { exception ->
                sendSideEffect(SearchResultMapSideEffect.RequestLocationPermission)
            }
        }
    }

    fun searchSpotByKeyword() {
        viewModelScope.launch {
            val keyword = state.value.keyword
            getCurrentLocationUseCase.invoke()
                .onSuccess { location ->
                    getSpot(keyword = keyword, userLocation = location, needMove = true)
                }
                .onFailure { e ->
                    when (e) {
                        is SecurityException -> {
                            val location = Location(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
                            getSpot(keyword = keyword, userLocation = location, needMove = true)
                        }
                    }
                }
        }
    }
    private fun searchSpotByMapLocation() {
        viewModelScope.launch {
            val keyword = state.value.keyword
            val mapScreenLocation = state.value.mapScreenLocation
            getCurrentLocationUseCase.invoke()
                .onSuccess { location ->
                    getSpot(keyword, mapScreenLocation, location)
                }.onFailure { e ->
                    when (e) {
                        is SecurityException -> {
                            val location = Location(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
                            getSpot(keyword, mapScreenLocation, location)
                        }
                    }
                }
        }
    }

    private suspend fun getSpot(
        keyword: String,
        mapScreenLocation: MapScreenLocation? = null,
        userLocation: Location,
        needMove: Boolean = false,
    ) {
        getSearchSpotsWithMapUseCase.invoke(
            keyword = keyword,
            minLatitude = mapScreenLocation?.minLatitude,
            minLongitude = mapScreenLocation?.minLongitude,
            maxLatitude = mapScreenLocation?.maxLatitude,
            maxLongitude = mapScreenLocation?.maxLongitude,
            userLatitude = userLocation.latitude,
            userLongitude = userLocation.longitude,
        ).onSuccess { spotWithMap ->
            val spot = spotWithMap.toUiModel()
            _state.update {
                it.copy(
                    searchSpot = spot,
                    spots = listOf(spot),
                    nearBySpotFetchEnabled = true,
                )
            }
            if (needMove) {
                sendSideEffect(
                    SearchResultMapSideEffect.UpdateCurrentLocation(
                        Location(
                            spot.latitude,
                            spot.longitude
                        )
                    )
                )
            }
        }.onFailure { e ->
            when (e) {
                is ApiException.NotFound -> {
                    sendSideEffect(SearchResultMapSideEffect.ShowNoResultAlert(e.message))
                }
                else -> {}
            }
            e.stackTrace
        }
    }

    private suspend fun fetchNearBySpots(categoryId: Int, withSearch: Boolean = false) {
        getCurrentLocationUseCase.invoke()
            .onSuccess { location ->
                updateCategorySpots(categoryId, state.value.mapScreenLocation, location, withSearch)
            }.onFailure { e ->
                when (e) {
                    is SecurityException -> {
                        val location = Location(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
                        updateCategorySpots(categoryId, state.value.mapScreenLocation, location, withSearch)
                    }
                }
            }
    }
    private suspend fun updateCategorySpots(
        categoryId: Int,
        mapScreenLocation: MapScreenLocation,
        userLocation: Location,
        withSearch: Boolean,
    ) {
        getCategorySpotsWithMapUseCase.invoke(
            categoryId = categoryId,
            minLatitude = mapScreenLocation.minLatitude,
            minLongitude = mapScreenLocation.minLongitude,
            maxLatitude = mapScreenLocation.maxLatitude,
            maxLongitude = mapScreenLocation.maxLongitude,
            userLatitude = userLocation.latitude,
            userLongitude = userLocation.longitude,
            withSearch = withSearch
        ).onSuccess { spots ->
            val searchSpotList = state.value.searchSpot
            val newSpots = searchSpotList?.let { listOf(it) + spots.toListUiModel() }?.distinct()

            _state.update {
                it.copy(
                    spots = newSpots,
                    selectedTabPosition = categoryId,
                )
            }
        }.onFailure { e ->
            e.stackTrace
        }
    }

    fun updateMapScreenLocation(mapScreenLocation: MapScreenLocation) {
        _state.update {
            it.copy(mapScreenLocation = mapScreenLocation)
        }
    }
    fun movedCameraPosition() {
        _state.update { it.copy(exploreVisible = true) }
    }

    fun onClickSearchBar(targetLocation: LatLng) {
        sendSideEffect(SearchResultMapSideEffect.NavigateSearch(targetLocation.toLocation()))
    }
    fun onClickCancelButton() {
        sendSideEffect(SearchResultMapSideEffect.NavigateAroundMe)
    }
    fun onClickCategoryTab(position: Int) {
        _state.update {
            it.copy(selectedTabPosition = position)
        }
    }
    fun onClickExploreThisArea() {
        searchSpotByMapLocation()
    }
    fun onClickAddLocationButton(targetLocation: LatLng) {
        sendSideEffect(SearchResultMapSideEffect.NavigateAddLocation(targetLocation.toLocation()))
    }

    private fun observeStateChanges() {
        viewModelScope.launch {
            launch {
                _state.map { it.selectedTabPosition }.distinctUntilChanged()
                    .collectLatest { selectedTabPosition ->
                        if (selectedTabPosition != null) {
                            fetchNearBySpots(selectedTabPosition)
                        }
                    }
            }
            launch {
                _state.map { it.mapScreenLocation }.distinctUntilChanged()
                    .collectLatest { _ ->
                        if (state.value.nearBySpotFetchEnabled) {
                            _state.update { it.copy(nearBySpotFetchEnabled = false) }
                            fetchNearBySpots(SpotCategory.ALL.id, true)
                        }
                    }
            }
        }
    }

    private fun sendSideEffect(effect: SearchResultMapSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class SearchResultMapState(
        val keyword: String = "",
        val searchSpot: SpotWithMapUiModel? = null,
        val spots: List<SpotWithMapUiModel>? = null,
        val nearBySpotFetchEnabled: Boolean = false,
        val currentTargetLocation: Location = Location(0.0, 0.0),
        val selectedTabPosition: Int? = null,
        val mapScreenLocation: MapScreenLocation = MapScreenLocation.getDefault(),
        val exploreVisible: Boolean = false,
        val errorMessage: String? = null,
        val isLoading: Boolean = false
    )

    sealed class SearchResultMapSideEffect {
        data object RequestLocationPermission : SearchResultMapSideEffect()
        data object NavigateAroundMe: SearchResultMapSideEffect()
        data class UpdateCurrentLocation(val location: Location): SearchResultMapSideEffect()
        data class ShowNoResultAlert(val message: String): SearchResultMapSideEffect()
        data class NavigateAddLocation(val location: Location): SearchResultMapSideEffect()
        data class NavigateSearch(val location: Location): SearchResultMapSideEffect()
    }
}