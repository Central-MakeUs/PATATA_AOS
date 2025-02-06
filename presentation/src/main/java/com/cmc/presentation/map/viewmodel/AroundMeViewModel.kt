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
import java.util.Date
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
                id = 1,
                name = "강북구청 전망대",
                description = "강북구를 한눈에 볼 수 있는 전망대입니다.",
                address = "서울특별시 강북구 도봉로 365",
                addressDetail = "강북구청 옥상",
                latitude = 37.6393,
                longitude = 127.0256,
                scraps = 120,
                deleted = false,
                createdAt = Date(),
                updatedAt = Date(),
                categoryId = 5,
                memberId = 101
            ),
            SpotUiModel(
                id = 2,
                name = "북한산 국립공원 입구",
                description = "자연 속에서 힐링할 수 있는 북한산 국립공원.",
                address = "서울특별시 강북구 우이동 산1",
                addressDetail = "우이동 등산로 입구",
                latitude = 37.6637,
                longitude = 127.0121,
                scraps = 200,
                deleted = false,
                createdAt = Date(),
                updatedAt = Date(),
                categoryId = 2,
                memberId = 102
            ),
            SpotUiModel(
                id = 3,
                name = "솔밭공원",
                description = "시원한 나무 그늘이 있는 강북구 솔밭공원.",
                address = "서울특별시 강북구 인수봉로 39",
                addressDetail = "솔밭역 2번 출구",
                latitude = 37.6565,
                longitude = 127.0138,
                scraps = 85,
                deleted = false,
                createdAt = Date(),
                updatedAt = Date(),
                categoryId = 3,
                memberId = 103
            ),
            SpotUiModel(
                id = 4,
                name = "4.19 민주묘지",
                description = "대한민국 민주화를 기리는 역사적인 장소.",
                address = "서울특별시 강북구 수유동 산48",
                addressDetail = "4.19 도로변",
                latitude = 37.6412,
                longitude = 127.0145,
                scraps = 150,
                deleted = false,
                createdAt = Date(),
                updatedAt = Date(),
                categoryId = 4,
                memberId = 104
            ),
            SpotUiModel(
                id = 5,
                name = "우이천 산책로",
                description = "강북구 주민들의 힐링 공간, 우이천 산책로.",
                address = "서울특별시 강북구 삼양로 93",
                addressDetail = "솔밭공원 인근",
                latitude = 37.6438,
                longitude = 127.0112,
                scraps = 90,
                deleted = false,
                createdAt = Date(),
                updatedAt = Date(),
                categoryId = 1,
                memberId = 105
            ),
            SpotUiModel(
                id = 6,
                name = "삼양사거리 야경",
                description = "서울 도심 속 아름다운 야경을 감상할 수 있는 곳.",
                address = "서울특별시 강북구 삼양로 312",
                addressDetail = "삼양사거리 부근",
                latitude = 37.6268,
                longitude = 127.0215,
                scraps = 70,
                deleted = false,
                createdAt = Date(),
                updatedAt = Date(),
                categoryId = 2,
                memberId = 106
            ),
            SpotUiModel(
                id = 7,
                name = "미아사거리 맛집 거리",
                description = "강북구에서 유명한 다양한 음식점들이 모여있는 거리.",
                address = "서울특별시 강북구 도봉로 101",
                addressDetail = "미아사거리역 인근",
                latitude = 37.6261,
                longitude = 127.0259,
                scraps = 180,
                deleted = false,
                createdAt = Date(),
                updatedAt = Date(),
                categoryId = 1,
                memberId = 107
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