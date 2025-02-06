package com.cmc.presentation.onboarding

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.feature.auth.usecase.GetAccessTokenUseCase
import com.cmc.domain.feature.auth.usecase.GetOnboardingStatusUseCase
import com.cmc.domain.feature.auth.usecase.GetRefreshTokenUseCase
import com.cmc.domain.feature.auth.usecase.RefreshTokenUseCase
import com.cmc.presentation.onboarding.SplashViewModel.SplashSideEffect.*
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
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getOnboardingStatusUseCase: GetOnboardingStatusUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val getRefreshTokenUseCase: GetRefreshTokenUseCase,
    private val refreshAccessTokenUseCase: RefreshTokenUseCase,
): ViewModel() {

    private val _state = MutableStateFlow<SplashState>(SplashState.Initialize)
    val state: StateFlow<SplashState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SplashSideEffect>()
    val sideEffect: SharedFlow<SplashSideEffect> = _sideEffect.asSharedFlow()

    fun getOnboardingStatus() {
        viewModelScope.launch {

            val onBoardingStateDeferred = async { getOnboardingStatusUseCase.invoke() }
            val accessTokenDeferred = async { getAccessTokenUseCase.invoke() }
            val refreshTokenDeferred = async { getRefreshTokenUseCase.invoke() }
            val delayDeferred = async { delay(2000L) }

            var onBoardingState: Boolean? = null
            var accessToken: String? = null
            var refreshToken: String? = null

            launch {
                onBoardingState = onBoardingStateDeferred.await()
                accessToken = accessTokenDeferred.await()
                refreshToken = refreshTokenDeferred.await()
            }

            delayDeferred.await()

            val effect = if (accessToken != null && isAccessTokenValid(accessToken!!)) {
                NavigateHome
            } else if (refreshToken != null) {
                NavigateOnBoarding
            } else if (onBoardingState != null && onBoardingState!!) {
                val result = refreshAccessTokenUseCase.invoke()
                if (result.isSuccess) NavigateHome
                else NavigateLogin
            } else {
                NavigateOnBoarding
            }
            _sideEffect.emit(effect)
        }
    }

    private fun isAccessTokenValid(accessToken: String): Boolean {
        try {
            // JWT 토큰의 중간 부분(Payload) 추출
            val parts = accessToken.split(".")
            if (parts.size != 3) return false

            val payloadEncoded = parts[1]

            // Base64 디코딩
            val payloadJson = String(Base64.decode(payloadEncoded, Base64.URL_SAFE), charset("UTF-8"))
            val payload = JSONObject(payloadJson)

            // exp 필드(만료 시간) 가져오기
            val exp = payload.getLong("exp")

            // 현재 시간과 비교 (exp는 보통 초 단위로 제공됨)
            val currentTime = System.currentTimeMillis() / 1000  // 밀리초를 초로 변환

            return currentTime < exp  // 현재 시간이 만료 시간보다 이르면 유효
        } catch (e: Exception) {
            e.printStackTrace()
            return false  // 디코딩 실패 시 무효로 간주
        }
    }

    sealed interface SplashState {
        data object Initialize: SplashState
        class Success(val onboardingCompleted: Boolean): SplashState
    }

    sealed class SplashSideEffect {
        data object NavigateOnBoarding: SplashSideEffect()
        data object NavigateLogin: SplashSideEffect()
        data object NavigateHome: SplashSideEffect()
    }
}
