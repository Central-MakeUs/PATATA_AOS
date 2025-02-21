package com.cmc.presentation.my.view

import androidx.fragment.app.viewModels
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentSignoutBinding
import com.cmc.presentation.my.viewmodel.SignOutViewModel
import com.cmc.presentation.my.viewmodel.SignOutViewModel.SignOutSideEffect
import com.cmc.presentation.my.viewmodel.SignOutViewModel.SignOutState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignOutFragment: BaseFragment<FragmentSignoutBinding>(R.layout.fragment_signout) {

    private val viewModel: SignOutViewModel by viewModels()

    override fun initObserving() {
        repeatWhenUiStarted {
            launch { viewModel.state.collect { state -> updateUI(state)} }
            launch { viewModel.sideEffect.collectLatest { effect -> handleSideEffect(effect)} }
        }
    }
    override fun initView() {
        binding.layoutInstructionsCheckButton.setOnClickListener {
            viewModel.onClickInstructionCheckButton()
        }
        binding.layoutInstructionsConfirmButton.setOnClickListener {
            viewModel.onClickConfirmButton()
        }
    }

    private fun updateUI(state: SignOutState) {
        binding.ivInstructionsCheck.isSelected = state.instructionsChecked
        binding.layoutInstructionsConfirmButton.isEnabled = state.isConfirmEnabled
    }
    private fun handleSideEffect(effect: SignOutSideEffect) {
        when (effect) {
            is SignOutSideEffect.NavigateLogin -> { navigateLogin() }
        }
    }

    private fun navigateLogin() { (activity as GlobalNavigation).navigateLogin() }
}