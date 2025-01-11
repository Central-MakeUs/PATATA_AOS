package com.cmc.presentation.splash

import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentSplashBinding


class SplashFragment : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {

    override fun initView() {
        binding.btnSplash.setOnClickListener {
            (activity as GlobalNavigation).navigateOnBoarding()
        }
    }

    override fun initObserving() {

    }
    
}