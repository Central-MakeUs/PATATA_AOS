package com.cmc.presentation.login.view

import androidx.fragment.app.viewModels
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentLoginBinding
import com.cmc.presentation.login.viewmodel.LoginViewModel

class LoginFragment: BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val viewModel: LoginViewModel by viewModels()

    override fun initView() {
        binding.btnLogin.setOnClickListener {
            (activity as GlobalNavigation).navigateHome()
        }
    }

    override fun initObserving() {

    }

}