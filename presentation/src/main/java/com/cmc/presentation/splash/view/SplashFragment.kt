package com.cmc.presentation.splash.view

import androidx.fragment.app.viewModels
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentSplashBinding
import com.cmc.presentation.splash.viewmodel.SplashViewModel
import kotlinx.coroutines.launch
import com.cmc.presentation.splash.viewmodel.SplashViewModel.SplashSideEffect
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
            is SplashSideEffect.NavigateHome -> navigateHome()
        }
    }

    private fun navigateOnBoarding() {
        (activity as GlobalNavigation).navigateOnBoarding()
    }
    private fun navigateLogin() {
        (activity as GlobalNavigation).navigateLogin()
    }
    private fun navigateHome() {
        (activity as GlobalNavigation).navigateHome()
    }
}