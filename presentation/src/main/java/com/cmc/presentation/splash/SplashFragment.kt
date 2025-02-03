package com.cmc.presentation.splash

import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashFragment : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {

    override fun initObserving() { }

    override fun initView() {
        repeatWhenUiStarted {
            launch {
                delay(2000)
                navigateOnBoarding()
            }
        }
    }

    private fun navigateOnBoarding() {
        (activity as GlobalNavigation).navigateOnBoarding()
    }
}