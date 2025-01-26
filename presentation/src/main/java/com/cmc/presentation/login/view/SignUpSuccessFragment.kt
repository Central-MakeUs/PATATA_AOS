package com.cmc.presentation.login.view

import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentSignUpSuccessBinding

class SignUpSuccessFragment: BaseFragment<FragmentSignUpSuccessBinding>(R.layout.fragment_sign_up_success) {
    override fun initObserving() {
    }

    override fun initView() {
        binding.layoutConfirmButton.setOnClickListener {
            (activity as GlobalNavigation).navigateHome()
        }
    }
}