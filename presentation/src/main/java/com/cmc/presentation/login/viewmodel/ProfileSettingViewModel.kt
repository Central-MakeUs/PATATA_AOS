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
class ProfileSettingViewModel @Inject constructor(
    private val updateNickNameUseCase: UpdateNickNameUseCase,
): ViewModel() {

    private val _state = MutableStateFlow<ProfileSettingState>(ProfileSettingState())
    val state: StateFlow<ProfileSettingState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<ProfileSettingSideEffect>()
    val sideEffect: SharedFlow<ProfileSettingSideEffect> = _sideEffect.asSharedFlow()

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
                _sideEffect.emit(ProfileSettingSideEffect.NavigateSignUpSuccess)
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

    private fun checkFormValid(state: ProfileSettingState): Boolean {
        return state.nickName.isBlank().not() &&
                state.isError.not()
    }


    data class ProfileSettingState(
        var nickName: String = "",
        var isError: Boolean = false,
        var isCompletedEnabled: Boolean = false
    )

    sealed class ProfileSettingSideEffect {
        data object NavigateSignUpSuccess: ProfileSettingSideEffect()
        data object NavigateHome: ProfileSettingSideEffect()
    }
}