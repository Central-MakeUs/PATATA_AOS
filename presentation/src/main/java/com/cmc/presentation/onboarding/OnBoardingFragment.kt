package com.cmc.presentation.onboarding

import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentOnboardingBinding

class OnBoardingFragment : BaseFragment<FragmentOnboardingBinding>(R.layout.fragment_onboarding) {

    override fun initView() {
        binding.btnOnboarding.setOnClickListener {
            (activity as GlobalNavigation).navigateLogin()
        }
    }

    override fun initObserving() {

    }

}