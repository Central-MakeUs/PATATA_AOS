package com.cmc.presentation.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.common.constants.PrimitiveValues.Location.DEFAULT_LATITUDE
import com.cmc.common.constants.PrimitiveValues.Location.DEFAULT_LONGITUDE
import com.cmc.domain.feature.location.GetCurrentLocationUseCase
import com.cmc.domain.feature.location.Location
import com.cmc.domain.feature.spot.usecase.GetCategorySpotsWithMapUseCase
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.map.model.MapScreenLocation
import com.cmc.presentation.map.model.SpotWithMapUiModel
import com.cmc.presentation.map.model.toListUiModel
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
class AroundMeViewModel @Inject constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getCategorySpotsWithMapUseCase: GetCategorySpotsWithMapUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(AroundMeState())
    val state: StateFlow<AroundMeState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<AroundMeSideEffect>()
    val sideEffect: SharedFlow<AroundMeSideEffect> = _sideEffect.asSharedFlow()

    init {
        observeStateChanges()
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            val result = getCurrentLocationUseCase.invoke()
            result.onSuccess { location ->
                sendSideEffect(AroundMeSideEffect.UpdateCurrentLocation(location))
            }.onFailure { exception ->
                sendSideEffect(AroundMeSideEffect.RequestLocationPermission)
            }
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

    fun invalidateCameraMove() {
        _state.update { it.copy(exploreVisible = false) }
    }

    fun onClickSearchBar(targetLocation: LatLng) {
        sendSideEffect(AroundMeSideEffect.NavigateSearch(targetLocation.toLocation()))
    }
    fun onClickCategoryTab(position: Int) {
        _state.update {
            it.copy(selectedTabPosition = position)
        }
    }
    fun onClickExploreThisArea() {
        _state.update {
            it.copy(selectedTabPosition = SpotCategory.ALL.id)
        }
    }
    fun onClickAddLocationButton(targetLocation: LatLng) {
        sendSideEffect(AroundMeSideEffect.NavigateAddLocation(targetLocation.toLocation()))
    }


    private fun observeStateChanges() {
        viewModelScope.launch {
            _state.map { it.selectedTabPosition }.distinctUntilChanged()
                .collectLatest { selectedTabPosition ->
                if (selectedTabPosition != null) {
                    getCurrentLocationUseCase.invoke()
                        .onSuccess { location ->
                            updateCategorySpots(selectedTabPosition, _state.value.mapScreenLocation, location)
                        }.onFailure { e ->
                            when (e) {
                                is SecurityException -> {
                                    val location = Location(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
                                    updateCategorySpots(selectedTabPosition, _state.value.mapScreenLocation, location)
                                }
                            }
                        }
                }
            }
        }
    }

    private fun sendSideEffect(effect: AroundMeSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    private suspend fun updateCategorySpots(
        categoryId: Int,
        mapScreenLocation: MapScreenLocation,
        userLocation: Location
    ) {
        getCategorySpotsWithMapUseCase.invoke(
            categoryId = categoryId,
            minLatitude = mapScreenLocation.minLatitude,
            minLongitude = mapScreenLocation.minLongitude,
            maxLatitude = mapScreenLocation.maxLatitude,
            maxLongitude = mapScreenLocation.maxLongitude,
            userLatitude = userLocation.latitude,
            userLongitude = userLocation.longitude,
            withSearch = false
        ).onSuccess { spots ->
            _state.update {
                it.copy(results = spots.toListUiModel())
            }
        }.onFailure { e ->
            e.stackTrace
        }
    }

    data class AroundMeState(
        val results: List<SpotWithMapUiModel>? = null,
        val selectedTabPosition: Int? = null,
        val mapScreenLocation: MapScreenLocation = MapScreenLocation.getDefault(),
        val exploreVisible: Boolean = false,
        val errorMessage: String? = null,
        val isLoading: Boolean = false
    )

    sealed class AroundMeSideEffect {
        data object RequestLocationPermission : AroundMeSideEffect()
        data class NavigateAddLocation(val location: Location): AroundMeSideEffect()
        data class NavigateSearch(val location: Location): AroundMeSideEffect()
        data class UpdateCurrentLocation(val location: Location): AroundMeSideEffect()
    }
}