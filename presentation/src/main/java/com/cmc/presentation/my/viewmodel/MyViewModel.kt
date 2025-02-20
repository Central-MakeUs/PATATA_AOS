package com.cmc.presentation.my.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.feature.spot.usecase.GetMySpotsUseCase
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.spot.model.SpotPreviewUiModel
import com.cmc.presentation.spot.model.toListUiModel
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
class MyViewModel @Inject constructor(
    private val getMySpotsUseCase: GetMySpotsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MyState())
    val state: StateFlow<MyState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<MySideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    init {
        fetchMySpots()
    }

    private fun fetchMySpots() {
        viewModelScope.launch {
            getMySpotsUseCase.invoke()
                .onSuccess { spots ->
                    _state.update {
                        it.copy(spots = spots.toListUiModel())
                    }
                }
                .onFailure {  }
        }
    }

    fun onClickSettingButton() {
        viewModelScope.launch {
            _sideEffect.emit(MySideEffect.NavigateToSetting)
        }
    }
    fun onClickExploreSpotButton() {
        sendSideEffect(MySideEffect.NavigateToCategorySpots(SpotCategory.ALL.id))
    }
    fun onClickSpotImage(spotId: Int) {
        sendSideEffect(MySideEffect.NavigateSpotDetail(spotId))
    }

    private fun sendSideEffect(effect: MySideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class MyState(
        val spots: List<SpotPreviewUiModel> = emptyList()
    )

    sealed class MySideEffect {
        data object NavigateToSetting : MySideEffect()
        data class NavigateToCategorySpots(val categoryId: Int) : MySideEffect()
        data class NavigateSpotDetail(val spotId: Int) : MySideEffect()
        data class ShowToast(val message: String) : MySideEffect()
    }
}