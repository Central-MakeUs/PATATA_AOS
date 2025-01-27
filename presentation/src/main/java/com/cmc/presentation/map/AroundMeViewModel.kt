package com.cmc.presentation.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class AroundMeViewModel @Inject constructor(): ViewModel() {

    private val _state = MutableStateFlow(TodaySpotRecommendationState())
    val state: StateFlow<TodaySpotRecommendationState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<AroundMeSideEffect>()
    val sideEffect: SharedFlow<AroundMeSideEffect> = _sideEffect.asSharedFlow()

    fun checkLocationPermission() {
        sendSideEffect(AroundMeSideEffect.RequestLocationPermission)
    }

    private fun sendSideEffect(effect: AroundMeSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class TodaySpotRecommendationState(
        val results: Int? = null,
        val errorMessage: String? = null,
        val searchStatus: AroundMeStatus = AroundMeStatus.LOADING
    )

    enum class AroundMeStatus {
        LOADING,
        SUCCESS,
        ERROR,
    }

    sealed class AroundMeSideEffect {
        data object RequestLocationPermission : AroundMeSideEffect()
    }
}