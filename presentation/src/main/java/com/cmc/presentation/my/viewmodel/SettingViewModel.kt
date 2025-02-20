package com.cmc.presentation.my.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class SettingViewModel @Inject constructor(): ViewModel() {

    private val _state: MutableStateFlow<SettingState> = MutableStateFlow(SettingState())
    val state: StateFlow<SettingState> = _state.asStateFlow()

    private val _sideEffect: MutableSharedFlow<SettingSideEffect> = MutableSharedFlow()
    val sideEffect: SharedFlow<SettingSideEffect> = _sideEffect.asSharedFlow()

    fun onClickFAQ() {
        sendSideEffect(SettingSideEffect.OpenNotionPage("https://www.notion.so/dogdduddy/FAQ-1a00ee442b80809094d4cc3414bc589b?pvs="))
    }

    private fun sendSideEffect(effect: SettingSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class SettingState(
        val versionCode: Int = 0,
    )
    sealed class SettingSideEffect {
        data object NavigateSignOut: SettingSideEffect()
        data object ShowDialog: SettingSideEffect()
        data class OpenNotionPage(val url: String): SettingSideEffect()
    }
}