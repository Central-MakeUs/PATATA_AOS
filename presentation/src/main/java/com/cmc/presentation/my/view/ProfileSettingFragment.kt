package com.cmc.presentation.my.view

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentProfileSettingBinding
import com.cmc.presentation.my.viewmodel.ProfileSettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.cmc.presentation.my.viewmodel.ProfileSettingViewModel.ProfileSettingState
import com.cmc.presentation.my.viewmodel.ProfileSettingViewModel.ProfileSettingSideEffect
import kotlinx.coroutines.flow.collectLatest
import com.cmc.common.constants.NavigationKeys.Setting

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
        initProfile(arguments)
        setAppbar()
        setEditText()
        setButtonListener()
    }

    private fun updateUI(state: ProfileSettingState) {
        binding.etEditTextInput.setErrorState(state.isNickNameError)
        Glide.with(binding.root)
            .load(state.uploadImage.uri)
            .circleCrop()
            .into(binding.ivProfileImage)

        binding.layoutCompleteButton.isEnabled = state.isCompletedEnabled
    }
    private fun handleSideEffect(effect: ProfileSettingSideEffect) {
        when (effect) {
            is ProfileSettingSideEffect.ShowPhotoPicker -> { pickImageLauncher.launch("image/*")}
            is ProfileSettingSideEffect.NavigateMyPage -> { navigateMyPage() }
            is ProfileSettingSideEffect.NavigateHome -> { navigateHome() }
        }
    }

    private fun initProfile(bundle: Bundle?) {
        bundle?.let {
            val nickName = it.getString(Setting.ARGUMENT_PROFILE_NICKNAME, "")
            val image = it.getString(Setting.ARGUMENT_PROFILE_IMAGE, "")
            binding.etEditTextInput.setEditorText(nickName)
            viewModel.initProfile(nickName, image)
        }
    }
    private fun setAppbar() {
        binding.appbar.setupAppBar(
            title = getString(R.string.title_profile_edit),
            onHeadButtonClick = { finish() },
        )
    }

    private fun setEditText() {
        binding.tvHelper.setText(getString(R.string.error_duplicate_nickname))
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
    private fun navigateMyPage() { finish() }
}