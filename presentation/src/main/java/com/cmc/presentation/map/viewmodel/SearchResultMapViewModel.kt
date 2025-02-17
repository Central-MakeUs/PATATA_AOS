package com.cmc.presentation.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.feature.location.GetCurrentLocationUseCase
import com.cmc.domain.feature.location.Location
import com.cmc.domain.feature.spot.usecase.GetCategorySpotsWithMapUseCase
import com.cmc.domain.feature.spot.usecase.GetSearchSpotsWithMapUseCase
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.map.model.MapScreenLocation
import com.cmc.presentation.map.model.SpotWithMapUiModel
import com.cmc.presentation.util.toLocation
import com.naver.maps.geometry.LatLng
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
class SearchResultMapViewModel @Inject constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getSearchSpotsWithMapUseCase: GetSearchSpotsWithMapUseCase,
    private val getCategorySpotsWithMapUseCase: GetCategorySpotsWithMapUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(SearchResultMapState())
    val state: StateFlow<SearchResultMapState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SearchResultMapSideEffect>()
    val sideEffect: SharedFlow<SearchResultMapSideEffect> = _sideEffect.asSharedFlow()

    fun initCurrentTargetLocation(location: Location) {
        _state.update { it.copy(currentTargetLocation = location) }
    }
    fun initCameraPosition() {
        sendSideEffect(SearchResultMapSideEffect.MoveCameraTarget(state.value.currentTargetLocation))
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
    fun movedCameraPosition() {
        _state.update { it.copy(exploreVisible = true) }
    }

    fun updateMapScreenLocation(mapScreenLocation: MapScreenLocation) {
        _state.update {
            it.copy(mapScreenLocation = mapScreenLocation)
        }
    }

    fun onClickSearchBar(targetLocation: LatLng) {
        sendSideEffect(SearchResultMapSideEffect.NavigateSearch(targetLocation.toLocation()))
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
        sendSideEffect(SearchResultMapSideEffect.NavigateAddLocation(targetLocation.toLocation()))
    }


    private fun sendSideEffect(effect: SearchResultMapSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class SearchResultMapState(
        val results: List<SpotWithMapUiModel>? = null,
        val currentTargetLocation: Location = Location(0.0, 0.0),
        val selectedTabPosition: Int? = null,
        val mapScreenLocation: MapScreenLocation = MapScreenLocation.getDefault(),
        val exploreVisible: Boolean = false,
        val errorMessage: String? = null,
        val isLoading: Boolean = false
    )

    sealed class SearchResultMapSideEffect {
        data object RequestLocationPermission : SearchResultMapSideEffect()
        data class NavigateAddLocation(val location: Location): SearchResultMapSideEffect()
        data class MoveCameraTarget(val location: Location): SearchResultMapSideEffect()
        data class NavigateSearch(val location: Location): SearchResultMapSideEffect()
        class UpdateCurrentLocation(val location: Location): SearchResultMapSideEffect()
    }
}