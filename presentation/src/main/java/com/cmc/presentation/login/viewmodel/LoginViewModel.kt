package com.cmc.presentation.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.feature.auth.usecase.LoginUseCase
import com.cmc.domain.base.exception.ApiException
import com.cmc.presentation.login.model.AuthResponseUiModel
import com.cmc.presentation.login.model.toUiModel
import com.cmc.presentation.spot.SpotDetailViewModel.SpotDetailSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val googleLoginUseCase: LoginUseCase,
): ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Initialize)
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<LoginSideEffect>()
    val sideEffect: SharedFlow<LoginSideEffect> = _sideEffect.asSharedFlow()

    suspend fun googleLogin(idToken: String) {
        googleLoginUseCase(idToken)
            .collectLatest { result ->
                result.onSuccess { user ->
                    _state.emit(LoginState.Success(user.toUiModel()))
                }.onFailure { error ->
                    when(error) {
                        is ApiException.NotFound -> { }
                        is ApiException.Unauthorized -> { }
                        is ApiException.ServerError -> { }
                    }
                }
            }
    }

    fun handleLoginResult(user: AuthResponseUiModel) {
        if (user.nickName.isNullOrEmpty().not()) {
            sendSideEffect(LoginSideEffect.NavigateToHome)
        } else {
            sendSideEffect(LoginSideEffect.NavigateToProfileSetting)
        }
    }

    private fun sendSideEffect(effect: LoginSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

}

sealed interface LoginState {
    data object Initialize : LoginState
    class Success(val user: AuthResponseUiModel) : LoginState
    class Error(val message : String) : LoginState
}

sealed class LoginSideEffect {
    data object NavigateToHome: LoginSideEffect()
    data object NavigateToProfileSetting: LoginSideEffect()
}