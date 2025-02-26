package com.cmc.presentation.login.view

import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentProfileInputBinding
import com.cmc.presentation.login.viewmodel.ProfileInputViewModel
import com.cmc.presentation.login.viewmodel.ProfileInputViewModel.ProfileInputSideEffect
import com.cmc.presentation.login.viewmodel.ProfileInputViewModel.ProfileInputState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
        setButtonListener()
    }

    private fun updateUI(state: ProfileInputState) {
        binding.etEditTextInput.setErrorState(state.isNickNameError)
        Glide.with(binding.root)
            .load(state.uploadImage.uri)
            .circleCrop()
            .into(binding.ivProfileImage)

        binding.layoutCompleteButton.isEnabled = state.isCompletedEnabled
    }
    private fun handleSideEffect(effect: ProfileInputSideEffect) {
        when (effect) {
            is ProfileInputSideEffect.ShowPhotoPicker -> { pickImageLauncher.launch("image/*")}
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
    private fun setButtonListener() {
        binding.layoutCompleteButton.setOnClickListener {
            viewModel.onClickCompleteButton()
        }
        binding.ivEditProfileImage.setOnClickListener {
            viewModel.onClickEditProfileImageButton()
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.setImage(it)
        }
    }

    private fun navigateHome() { (activity as GlobalNavigation).navigateHome() }
    private fun navigateSignUpSuccess() { navigate(R.id.navigate_signup_success) }
}