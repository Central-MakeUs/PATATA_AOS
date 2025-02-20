package com.cmc.presentation.login.view

import androidx.fragment.app.viewModels
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentProfileInputBinding
import com.cmc.presentation.login.viewmodel.ProfileInputViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.cmc.presentation.login.viewmodel.ProfileInputViewModel.ProfileInputState
import com.cmc.presentation.login.viewmodel.ProfileInputViewModel.ProfileInputSideEffect
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileInputFragment: BaseFragment<FragmentProfileInputBinding>(R.layout.fragment_profile_input) {

    private val viewModel: ProfileInputViewModel by viewModels()

    override fun initObserving() {
        repeatWhenUiStarted {
            launch { viewModel.state.collect { state -> updateUI(state) } }
            launch { viewModel.sideEffect.collectLatest { effect -> handleSideEffect(effect) } }
        }
    }

    override fun initView() {
        setAppbar()
        setEditText()
        setProfileImage()
        setButtonListener()
    }

    private fun updateUI(state: ProfileInputState) {
        binding.etEditTextInput.setErrorState(state.isError)
    }
    private fun handleSideEffect(effect: ProfileInputSideEffect) {
        when (effect) {
            is ProfileInputSideEffect.NavigateSignUpSuccess -> { navigateSignUpSuccess() }
            is ProfileInputSideEffect.NavigateHome -> { navigateHome() }
        }
    }

    private fun setAppbar() {
        binding.appbar.setupAppBar(
            title = getString(R.string.title_profile_setting),
            onHeadButtonClick = { finish() },
        )
    }

    private fun setEditText() {
        binding.tvHelper.setText("이미 사용 중인 닉네임입니다.")
        binding.etEditTextInput.apply {
            connectHelperView(binding.tvHelper)
            setOnTextChangeListener { str ->
                viewModel.setNickName(str)
            }
        }
    }

    private fun setProfileImage() {
        // TODO: 프로필 이미지 반영
    }

    private fun setButtonListener() {
        binding.layoutCompleteButton.setOnClickListener {
            viewModel.onClickCompleteButton()
        }
    }

    private fun navigateHome() { (activity as GlobalNavigation).navigateHome() }
    private fun navigateSignUpSuccess() { navigate(R.id.navigate_signup_success) }
}