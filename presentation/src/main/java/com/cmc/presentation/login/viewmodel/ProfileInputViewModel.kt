package com.cmc.presentation.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.feature.auth.usecase.UpdateNickNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileInputViewModel @Inject constructor(
    private val updateNickNameUseCase: UpdateNickNameUseCase,
): ViewModel() {

    private val _state = MutableStateFlow<ProfileInputState>(ProfileInputState())
    val state: StateFlow<ProfileInputState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<ProfileInputSideEffect>()
    val sideEffect: SharedFlow<ProfileInputSideEffect> = _sideEffect.asSharedFlow()

    init {
        observeStateChanges()
    }

    fun setNickName(nickName: String) {
        if (state.value.nickName == nickName) return
        _state.update {
            it.copy(
                isError = false,
                nickName = nickName
            )
        }
    }

    fun onClickCompleteButton() {
        viewModelScope.launch {
            val result = updateNickNameUseCase.invoke(state.value.nickName)
            if (result.isSuccess) {
                _sideEffect.emit(ProfileInputSideEffect.NavigateSignUpSuccess)
            } else {
                _state.update {
                    it.copy(
                        isError = true
                    )
                }
            }
        }
    }

    private fun observeStateChanges() {
        viewModelScope.launch {
            _state.collectLatest { currentState ->
                val isFormValid = checkFormValid(currentState)
                if (currentState.isCompletedEnabled != isFormValid) {
                    _state.update { it.copy(isCompletedEnabled = isFormValid) }
                }
            }
        }
    }

    private fun checkFormValid(state: ProfileInputState): Boolean {
        return state.nickName.isBlank().not() &&
                state.isError.not()
    }


    data class ProfileInputState(
        var nickName: String = "",
        var isError: Boolean = false,
        var isCompletedEnabled: Boolean = false
    )

    sealed class ProfileInputSideEffect {
        data object NavigateSignUpSuccess: ProfileInputSideEffect()
        data object NavigateHome: ProfileInputSideEffect()
    }
}