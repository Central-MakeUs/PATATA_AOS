package com.cmc.presentation.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.feature.location.Location
import com.cmc.presentation.map.model.MapScreenLocation
import com.cmc.presentation.map.model.SpotWithMapUiModel
import com.cmc.presentation.map.viewmodel.AroundMeViewModel.AroundMeSideEffect
import com.cmc.presentation.map.viewmodel.AroundMeViewModel.AroundMeState
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultMapViewModel @Inject constructor(

): ViewModel() {

    private val _state = MutableStateFlow(SearchResultMapState())
    val state: StateFlow<SearchResultMapState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SearchResultMapSideEffect>()
    val sideEffect: SharedFlow<SearchResultMapSideEffect> = _sideEffect.asSharedFlow()



    private fun sendSideEffect(effect: SearchResultMapSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class SearchResultMapState(
        val results: List<SpotWithMapUiModel>? = null,
        val selectedTabPosition: Int? = null,
        val mapScreenLocation: MapScreenLocation = MapScreenLocation.getDefault(),
        val exploreVisible: Boolean = false,
        val errorMessage: String? = null,
        val isLoading: Boolean = false
    )

    sealed class SearchResultMapSideEffect {
        data object RequestLocationPermission : SearchResultMapSideEffect()
        data object NavigateAddLocation: SearchResultMapSideEffect()
        data class NavigateSearch(val targetTarget: LatLng): SearchResultMapSideEffect()
        class UpdateCurrentLocation(val location: Location): SearchResultMapSideEffect()
    }
}