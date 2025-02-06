package com.cmc.presentation.login.view

import androidx.fragment.app.viewModels
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentProfileSettingBinding
import com.cmc.presentation.login.viewmodel.ProfileSettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import com.cmc.presentation.login.viewmodel.ProfileSettingViewModel.ProfileSettingState
import com.cmc.presentation.login.viewmodel.ProfileSettingViewModel.ProfileSettingSideEffect
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileSettingFragment: BaseFragment<FragmentProfileSettingBinding>(R.layout.fragment_profile_setting) {

    private val viewModel: ProfileSettingViewModel by viewModels()

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

    private fun updateUI(state: ProfileSettingState) {
        binding.etEditTextInput.setErrorState(state.isError)
    }
    private fun handleSideEffect(effect: ProfileSettingSideEffect) {
        when (effect) {
            is ProfileSettingSideEffect.NavigateSignUpSuccess -> { navigateSignUpSuccess() }
            is ProfileSettingSideEffect.NavigateHome -> { navigateHome() }
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