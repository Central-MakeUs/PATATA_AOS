package com.cmc.presentation.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.auth.usecase.LoginUseCase
import com.cmc.domain.exception.ApiException
import com.cmc.presentation.login.model.UserUiModel
import com.cmc.presentation.login.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val googleLoginUseCase: LoginUseCase,
): ViewModel() {

    private val _userState = MutableStateFlow<UserUiModel?>(null)
    val userState: StateFlow<UserUiModel?> = _userState

    suspend fun googleLogin(idToken: String) {
        googleLoginUseCase(idToken)
            .collectLatest { result ->
                result.onSuccess { user ->
                    _userState.emit(user.toUiModel())
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