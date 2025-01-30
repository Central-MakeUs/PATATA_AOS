package com.cmc.presentation.login.view

import com.cmc.common.base.BaseFragment
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentProfileSettingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileSettingFragment: BaseFragment<FragmentProfileSettingBinding>(R.layout.fragment_profile_setting) {
    override fun initObserving() {
    }

    override fun initView() {
        setAppbar()
        setEditText()
        setProfileImage()
        setButtonListener()
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
            setOnSubmitListener {
                // TODO: 닉네임 중복 검증
                // TODO: 닉네임 미중복 시 setButtonEnable(true)
                // TODO: 닉네임 중복 시 setErrorState(true)
                // Button Enable 상태에서 텍스트 변경 시 Button Enable 변경 여부 확인
                setErrorState(true)
            }
        }
    }

    private fun setProfileImage() {
        // TODO: 프로필 이미지 반영
    }

    private fun setButtonListener() {
        binding.layoutCompleteButton.setOnClickListener {
            // TODO: 닉네임 업데이트 API
        }
    }

    private fun setButtonEnable(isEnabled: Boolean) {
        binding.layoutCompleteButton.isEnabled = isEnabled
    }

}