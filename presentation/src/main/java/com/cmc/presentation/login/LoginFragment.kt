package com.cmc.presentation.login

import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentLoginBinding

class LoginFragment: BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {
    override fun initView() {
        binding.btnLogin.setOnClickListener {
            (activity as GlobalNavigation).navigateHome()
        }
    }

    override fun initObserving() {

    }

}