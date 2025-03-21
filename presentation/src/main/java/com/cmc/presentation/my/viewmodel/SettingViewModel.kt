package com.cmc.presentation.my.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.constants.AppUrlPolicy
import com.cmc.domain.feature.auth.usecase.ClearTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val clearTokenUseCase: ClearTokenUseCase,
): ViewModel() {

    private val _state: MutableStateFlow<SettingState> = MutableStateFlow(SettingState())
    val state: StateFlow<SettingState> = _state.asStateFlow()

    private val _sideEffect: MutableSharedFlow<SettingSideEffect> = MutableSharedFlow()
    val sideEffect: SharedFlow<SettingSideEffect> = _sideEffect.asSharedFlow()

    fun onClickReview() {
        sendSideEffect(SettingSideEffect.OpenAppReview)
    }
    fun onClickTermsOfService() {
        sendSideEffect(SettingSideEffect.OpenNotionPage(AppUrlPolicy.TERMS_OF_SERVICE_URL))
    }
    fun onClickPrivacy() {
        sendSideEffect(SettingSideEffect.OpenNotionPage(AppUrlPolicy.PRIVACY_URL))
    }
    fun onClickOpenSourceLicense()  {
        sendSideEffect(SettingSideEffect.OpenNotionPage(AppUrlPolicy.OPEN_SOURCE_LICENSE_URL))
    }
    fun onClickNotices()  {
        sendSideEffect(SettingSideEffect.OpenNotionPage(AppUrlPolicy.NOTICES_URL))
    }
    fun onClickFAQ() {
        sendSideEffect(SettingSideEffect.OpenNotionPage(AppUrlPolicy.FAQ_URL))
    }
    fun onClickContactUse()  {
        sendSideEffect(SettingSideEffect.OpenNotionPage(AppUrlPolicy.CONTACT_USE_URL))
    }
    fun onClickLogoutButton() {
        viewModelScope.launch {
            clearTokenUseCase.invoke()
            sendSideEffect(SettingSideEffect.NavigateLogin)
        }
    }
    fun onClickSignOutButton() {
        sendSideEffect(SettingSideEffect.NavigateSignOut)
    }
    private fun sendSideEffect(effect: SettingSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }
    fun onClickHeadButton() {
        sendSideEffect(SettingSideEffect.Finish)
    }

    data class SettingState(
        val versionCode: Int = 0,
    )
    sealed class SettingSideEffect {
        data object Finish: SettingSideEffect()
        data object NavigateSignOut: SettingSideEffect()
        data object NavigateLogin :SettingSideEffect()
        data object ShowDialog: SettingSideEffect()
        data object OpenAppReview: SettingSideEffect()
        data class OpenNotionPage(val url: String): SettingSideEffect()
    }
}