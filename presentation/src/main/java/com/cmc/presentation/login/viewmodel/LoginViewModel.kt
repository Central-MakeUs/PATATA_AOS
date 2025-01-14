package com.cmc.presentation.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.auth.usecase.LoginUseCase
import com.cmc.presentation.login.model.UserUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
        _userState.emit(UserUiModel.toUiModel(googleLoginUseCase(idToken)))
    }
}