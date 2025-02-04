package com.cmc.presentation.splash

import androidx.fragment.app.viewModels
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentSplashBinding
import com.cmc.presentation.onboarding.SplashViewModel
import kotlinx.coroutines.launch
import com.cmc.presentation.onboarding.SplashViewModel.SplashSideEffect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels()

    override fun initObserving() {
        repeatWhenUiStarted {
            viewModel.sideEffect.collectLatest { effect -> handleSideEffect(effect) }
        }
    }

    override fun initView() {
        repeatWhenUiStarted {
            launch {
                viewModel.getOnboardingStatus()
            }
        }
    }

    private fun handleSideEffect(effect: SplashSideEffect) {
        when (effect) {
            is SplashSideEffect.NavigateOnBoarding -> navigateOnBoarding()
            is SplashSideEffect.NavigateLogin -> navigateLogin()
        }
    }

    private fun navigateOnBoarding() {
        (activity as GlobalNavigation).navigateOnBoarding()
    }
    private fun navigateLogin() {
        (activity as GlobalNavigation).navigateLogin()
    }
}