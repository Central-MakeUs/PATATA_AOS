package com.cmc.presentation.splash.view

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentSplashBinding
import com.cmc.presentation.splash.viewmodel.SplashViewModel
import kotlinx.coroutines.launch
import com.cmc.presentation.splash.viewmodel.SplashViewModel.SplashSideEffect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels()

    override fun initObserving() {
        repeatWhenUiStarted {
            viewModel.sideEffect.collectLatest { effect -> handleSideEffect(effect) }
        }
    }

    override fun initView() {
        checkLocationRequest()
    }

    private fun handleSideEffect(effect: SplashSideEffect) {
        when (effect) {
            is SplashSideEffect.NavigateOnBoarding -> navigateOnBoarding()
            is SplashSideEffect.NavigateLogin -> navigateLogin()
            is SplashSideEffect.NavigateHome -> navigateHome()
        }
    }
    private fun checkLocationRequest() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (permissions.all {
                ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
            }) {
            locationPermissionRequest.launch(permissions)
        } else {
            viewModel.getOnboardingStatus()
        }
    }
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            Toast.makeText(context, "위치 권한이 허용되었습니다!", Toast.LENGTH_SHORT).show()
        }
        viewModel.getOnboardingStatus()
    }

    private fun navigateOnBoarding() {
        (activity as GlobalNavigation).navigateOnBoarding()
    }
    private fun navigateLogin() {
        (activity as GlobalNavigation).navigateLogin()
    }
    private fun navigateHome() {
        (activity as GlobalNavigation).navigateHome()
    }
}