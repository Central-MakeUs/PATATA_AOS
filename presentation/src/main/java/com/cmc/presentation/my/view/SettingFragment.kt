package com.cmc.presentation.my.view

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.fragment.app.viewModels
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentSettingBinding
import com.cmc.presentation.my.viewmodel.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.cmc.presentation.my.viewmodel.SettingViewModel.SettingState
import com.cmc.presentation.my.viewmodel.SettingViewModel.SettingSideEffect

@AndroidEntryPoint
class SettingFragment: BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {

    private val viewModel: SettingViewModel by viewModels()

    override fun initObserving() {
        repeatWhenUiStarted {
            launch { viewModel.state.collect { state -> updateUI(state) } }
            launch { viewModel.sideEffect.collectLatest { effect -> handleSideEffect(effect) } }
        }
    }

    override fun initView() {
        setAppBar()
        initCode()
        setButton()
    }
    private fun setAppBar() {
        binding.settingAppbar.setupAppBar(
            title = getString(R.string.setting),
            onHeadButtonClick = { viewModel.onClickHeadButton() }
        )
    }
    private fun updateUI(state: SettingState) {

    }
    private fun handleSideEffect(effect: SettingSideEffect) {
        when (effect) {
            is SettingSideEffect.Finish -> { finish() }
            is SettingSideEffect.NavigateSignOut -> { navigateSignOut() }
            is SettingSideEffect.NavigateLogin -> { navigateLogin() }
            is SettingSideEffect.ShowDialog -> {}
            is SettingSideEffect.OpenNotionPage -> { openNotionPage(effect.url) }
        }
    }

    private fun initCode() {
        binding.tvSettingVersion.text = getAppVersion()
    }
    private fun setButton() {
        with(binding) {
            layoutTerms.setOnClickListener { viewModel.onClickFAQ() }
            layoutPrivacyPolicy.setOnClickListener { viewModel.onClickFAQ() }
            layoutOpenSourceLicense.setOnClickListener { viewModel.onClickFAQ() }
            layoutSettingNotice.setOnClickListener { viewModel.onClickFAQ() }
            layoutSettingFaq.setOnClickListener { viewModel.onClickFAQ() }
            layoutSettingContact.setOnClickListener { viewModel.onClickFAQ() }

            tvLogout.setOnClickListener { viewModel.onClickLogoutButton() }
            tvSignout.setOnClickListener { viewModel.onClickSignOutButton() }
        }
    }
    private fun getAppVersion(): String {
        return try {
            val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
            packageInfo.versionName // "1.0.0" 같은 형식의 버전명 반환
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }
    private fun openNotionPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        requireContext().startActivity(intent)
    }

    private fun navigateLogin() { (activity as GlobalNavigation).navigateLogin() }
    private fun navigateSignOut() { navigate(R.id.navigate_signout) }
}