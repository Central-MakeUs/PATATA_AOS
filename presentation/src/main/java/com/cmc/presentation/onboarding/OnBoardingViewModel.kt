package com.cmc.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.feature.auth.usecase.SetOnBoardingStatusUseCase
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
class OnBoardingViewModel @Inject constructor(
    private val setOnBoardingStatusUseCase: SetOnBoardingStatusUseCase,
): ViewModel() {

    private val _state = MutableStateFlow<OnBoardingState>(OnBoardingState())
    val state: StateFlow<OnBoardingState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<OnBoardingSideEffect>()
    val sideEffect: SharedFlow<OnBoardingSideEffect> = _sideEffect.asSharedFlow()

    fun onClickNextButton(maxCount: Int) {
        val currentPagePosition = _state.value.currentPagePosition
        if (currentPagePosition < maxCount - 1) {
            _state.update {
                it.copy(currentPagePosition = currentPagePosition + 1)
            }
        } else {
            viewModelScope.launch {
                setOnBoardingStatusUseCase.invoke(true)
                    .onSuccess {
                        _sideEffect.emit(OnBoardingSideEffect.NavigateLogin)
                    }
            }
        }
    }

    fun backPressed() {
        val currentPagePosition = _state.value.currentPagePosition
        if (currentPagePosition != 0) {
            _state.update {
                it.copy(currentPagePosition = currentPagePosition - 1)
            }
        } else {
            viewModelScope.launch {
                _sideEffect.emit(OnBoardingSideEffect.Finish)
            }
        }
    }

    data class OnBoardingState(
        var currentPagePosition: Int = 0,
    )

    sealed class OnBoardingSideEffect {
        data object NavigateLogin: OnBoardingSideEffect()
        data object Finish: OnBoardingSideEffect()
    }
}
