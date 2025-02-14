package com.cmc.presentation.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.feature.location.GetCurrentLocationUseCase
import com.cmc.domain.feature.location.Location
import com.cmc.presentation.model.SpotUiModel
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
class AroundMeViewModel @Inject constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(AroundMeState())
    val state: StateFlow<AroundMeState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<AroundMeSideEffect>()
    val sideEffect: SharedFlow<AroundMeSideEffect> = _sideEffect.asSharedFlow()

    fun checkLocationPermission() {
        sendSideEffect(AroundMeSideEffect.RequestLocationPermission)
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            val result = getCurrentLocationUseCase.invoke()
            result.onSuccess { location ->
                sendSideEffect(AroundMeSideEffect.UpdateCurrentLocation(location))
            }.onFailure { exception ->
                // TODO: exception 별 처리 추가
                sendSideEffect(AroundMeSideEffect.RequestLocationPermission)
            }
        }
    }

    fun getDumpData() {
        val dummySpots = listOf(
            SpotUiModel(
                spotId = 1,
                spotName = "강북구청 전망대",
                address = "서울특별시 강북구 도봉로 365",
//                latitude = 37.6393,
//                longitude = 127.0256,
                tags = emptyList(),
                categoryId = 5,
            ),
            SpotUiModel(
                spotId = 1,
                spotName = "강북구청 전망대",
                address = "서울특별시 강북구 도봉로 365",
//                latitude = 37.6393,
//                longitude = 127.0256,
                tags = emptyList(),
                categoryId = 5,
            ),
            SpotUiModel(
                spotId = 1,
                spotName = "강북구청 전망대",
                address = "서울특별시 강북구 도봉로 365",
//                latitude = 37.6393,
//                longitude = 127.0256,
                tags = emptyList(),
                categoryId = 5,
            ),
            SpotUiModel(
                spotId = 1,
                spotName = "강북구청 전망대",
                address = "서울특별시 강북구 도봉로 365",
//                latitude = 37.6393,
//                longitude = 127.0256,
                tags = emptyList(),
                categoryId = 5,
            ),
            SpotUiModel(
                spotId = 1,
                spotName = "강북구청 전망대",
                address = "서울특별시 강북구 도봉로 365",
//                latitude = 37.6393,
//                longitude = 127.0256,
                tags = emptyList(),
                categoryId = 5,
            ),
            SpotUiModel(
                spotId = 1,
                spotName = "강북구청 전망대",
                address = "서울특별시 강북구 도봉로 365",
//                latitude = 37.6393,
//                longitude = 127.0256,
                tags = emptyList(),
                categoryId = 5,
            ),
            SpotUiModel(
                spotId = 1,
                spotName = "강북구청 전망대",
                address = "서울특별시 강북구 도봉로 365",
//                latitude = 37.6393,
//                longitude = 127.0256,
                tags = emptyList(),
                categoryId = 5,
            )
        )
        viewModelScope.launch {
            _state.update {
                state.value.copy(
                    results = dummySpots,
                )
            }
        }
    }

    private fun sendSideEffect(effect: AroundMeSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class AroundMeState(
        val results: List<SpotUiModel>? = null,
        val errorMessage: String? = null,
        val isLoading: Boolean = false
    )

    sealed class AroundMeSideEffect {
        data object RequestLocationPermission : AroundMeSideEffect()
        class UpdateCurrentLocation(val location: Location): AroundMeSideEffect()
    }
}