package com.cmc.presentation.my.viewmodel

import android.accounts.Account
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.feature.auth.usecase.ClearTokenUseCase
import com.cmc.domain.feature.auth.usecase.GetMyProfileUseCase
import com.cmc.domain.feature.auth.usecase.SignOutGoogleUseCase
import com.cmc.presentation.login.model.toUiModel
import com.google.android.gms.auth.GoogleAuthUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignOutViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val signOutGoogleUseCase: SignOutGoogleUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val clearTokenUseCase: ClearTokenUseCase,
): ViewModel() {

    private val _state: MutableStateFlow<SignOutState> = MutableStateFlow(SignOutState())
    val state: StateFlow<SignOutState> = _state.asStateFlow()

    private val _sideEffect: MutableSharedFlow<SignOutSideEffect> = MutableSharedFlow()
    val sideEffect: SharedFlow<SignOutSideEffect> = _sideEffect.asSharedFlow()

    init {
        observeStateChanges()
    }

    fun onClickInstructionCheckButton() {
        _state.update {
            it.copy(
                instructionsChecked = it.instructionsChecked.not()
            )
        }
    }
    fun onClickConfirmButton() {
        viewModelScope.launch {
            getMyProfileUseCase.invoke()
                .onSuccess { member ->
                    val memberUiModel = member.toUiModel()

                    val scope = "oauth2:https://www.googleapis.com/auth/userinfo.profile"
                    val account = Account(memberUiModel.email, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE)
                    val accessToken = withContext(Dispatchers.IO) {
                        GoogleAuthUtil.getToken(context, account, scope)
                    }

                    signOutGoogleUseCase.invoke(accessToken)
                        .onSuccess {
                            clearTokenUseCase.invoke()
                            sendSideEffect(SignOutSideEffect.NavigateLogin)
                        }.onFailure { e ->
                            e.printStackTrace()
                        }
                }.onFailure { e ->
                    e.printStackTrace()
                }
        }
    }

    private fun observeStateChanges() {
        viewModelScope.launch {
            _state.collectLatest { currentState ->
                val isConfirmEnabled = checkFormValid(currentState)
                _state.update {
                    it.copy(isConfirmEnabled = isConfirmEnabled)
                }
            }
        }
    }

    private fun checkFormValid(state: SignOutState): Boolean {
        return state.instructionsChecked
    }

    private fun sendSideEffect(effect: SignOutSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class SignOutState(
        val versionCode: Int = 0,
        val instructionsChecked: Boolean = false,
        val isConfirmEnabled: Boolean = false,
    )
    sealed class SignOutSideEffect {
        data object NavigateLogin :SignOutSideEffect()
    }

}