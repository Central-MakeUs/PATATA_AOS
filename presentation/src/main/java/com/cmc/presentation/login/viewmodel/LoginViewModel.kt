package com.cmc.presentation.login.viewmodel

import androidx.lifecycle.ViewModel
import com.cmc.domain.auth.usecase.LoginUseCase
import com.cmc.domain.exception.ApiException
import com.cmc.presentation.login.model.AuthResponseUiModel
import com.cmc.presentation.login.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val googleLoginUseCase: LoginUseCase,
): ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Initialize)
    val state: StateFlow<LoginState> = _state.asStateFlow()

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
}

sealed interface LoginState {
    data object Initialize : LoginState
    class Success(val user: AuthResponseUiModel) : LoginState
    class Error(val message : String) : LoginState
}