package com.cmc.presentation.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.auth.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val googleLoginUseCase: LoginUseCase,
): ViewModel() {

    fun googleLogin(idToken: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                googleLoginUseCase(idToken)
            }
        }
    }
}