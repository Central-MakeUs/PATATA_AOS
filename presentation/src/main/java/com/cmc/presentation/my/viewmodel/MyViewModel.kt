package com.cmc.presentation.my.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.model.SpotCategory
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
class MyViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(MyState())
    val state: StateFlow<MyState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<MySideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()


    fun onClickSettingButton() {
        viewModelScope.launch {
            _sideEffect.emit(MySideEffect.NavigateToSetting)
        }
    }
    fun onClickExploreSpotButton() {
        sendSideEffect(MySideEffect.NavigateToCategorySpots(SpotCategory.ALL.id))
    }

    private fun sendSideEffect(effect: MySideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class MyState(
        val images: List<String> = emptyList()
    )

    sealed class MySideEffect {
        data object NavigateToSetting : MySideEffect()
        data class NavigateToCategorySpots(val categoryId: Int) : MySideEffect()
        data class ShowToast(val message: String) : MySideEffect()
    }
}