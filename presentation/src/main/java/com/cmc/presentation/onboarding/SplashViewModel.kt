package com.cmc.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.feature.auth.usecase.GetOnboardingStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getOnboardingStatusUseCase: GetOnboardingStatusUseCase,
): ViewModel() {

    private val _state = MutableStateFlow<SplashState>(SplashState.Initialize)
    val state: StateFlow<SplashState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SplashSideEffect>()
    val sideEffect: SharedFlow<SplashSideEffect> = _sideEffect.asSharedFlow()

    fun getOnboardingStatus() {
        viewModelScope.launch {

            val apiDeferred = async { getOnboardingStatusUseCase.invoke() }
            val delayDeferred = async { delay(2000L) }

            var apiResult: Boolean? = null

            launch {
                apiResult = apiDeferred.await()
            }

            delayDeferred.await()

            if (apiResult != null) {
                if (apiResult == true) {
                    _sideEffect.emit(SplashSideEffect.NavigateLogin)
                } else {
                    _sideEffect.emit(SplashSideEffect.NavigateOnBoarding)
                }
            } else {
                _sideEffect.emit(SplashSideEffect.NavigateOnBoarding)
            }
        }
    }

    sealed interface SplashState {
        data object Initialize: SplashState
        class Success(val onboardingCompleted: Boolean): SplashState
    }

    sealed class SplashSideEffect {
        data object NavigateOnBoarding: SplashSideEffect()
        data object NavigateLogin: SplashSideEffect()
    }
}
