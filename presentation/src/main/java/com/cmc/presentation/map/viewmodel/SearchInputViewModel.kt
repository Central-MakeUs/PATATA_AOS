package com.cmc.presentation.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.feature.location.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchInputViewModel @Inject constructor(): ViewModel() {

    private val _state = MutableStateFlow(SearchInputState())
    val state: StateFlow<SearchInputState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SearchInputSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    fun submitSearchBar(keyword: String) {
        sendSideEffect(SearchInputSideEffect.NavigateSearchResultMap(keyword, _state.value.targetLocation))
    }
    fun onClickHeaderButton() {
        sendSideEffect(SearchInputSideEffect.Finish)
    }
    fun setTargetLocation(latitude: Double, longitude: Double) {
        _state.update {
            it.copy(targetLocation = Location(latitude = latitude, longitude = longitude))
        }
    }

    private fun sendSideEffect(effect: SearchInputSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class SearchInputState(
        val keyword: String = "",
        val targetLocation: Location = Location(0.0, 0.0)
    )
    sealed class SearchInputSideEffect {
        data class NavigateSearchResultMap(val keyword: String, val location: Location): SearchInputSideEffect()
        data object Finish: SearchInputSideEffect()
    }
}