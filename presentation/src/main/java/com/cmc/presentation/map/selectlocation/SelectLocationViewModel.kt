package com.cmc.presentation.map.selectlocation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SelectLocationViewModel @Inject constructor(): ViewModel() {

    private val _state = MutableStateFlow(SelectLocationState())
    val state: StateFlow<SelectLocationState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SelectLocationSideEffect>()
    val sideEffect: SharedFlow<SelectLocationSideEffect> = _sideEffect.asSharedFlow()



    data class SelectLocationState(
        val isLoading: Boolean = false
    )

    sealed class SelectLocationSideEffect {
        data object NavigateToAddSpot : SelectLocationSideEffect()
    }
}