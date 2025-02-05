package com.cmc.presentation.onboarding

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentOnboardingBinding
import com.cmc.presentation.onboarding.OnBoardingViewModel.OnBoardingSideEffect
import com.cmc.presentation.onboarding.OnBoardingViewModel.OnBoardingState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnBoardingFragment : BaseFragment<FragmentOnboardingBinding>(R.layout.fragment_onboarding) {

    private val viewModel: OnBoardingViewModel by viewModels()

    private lateinit var adapter: OnBoardingPagerAdapter


    override fun initObserving() {
        repeatWhenUiStarted {
            launch { viewModel.state.collect { state -> updateUI(state) } }
            launch { viewModel.sideEffect.collectLatest { effect -> handleSideEffect(effect) } }
        }
    }
    override fun initView() {
        setViewPager()
        setNextButton()
        overrideBackPressed()
    }

    private fun updateUI(state: OnBoardingState) {
        binding.viewpager.currentItem = state.currentPagePosition
    }
    private fun handleSideEffect(effect: OnBoardingSideEffect) {
        when (effect) {
            is OnBoardingSideEffect.NavigateLogin -> { navigateLogin() }
            is OnBoardingSideEffect.Finish -> { finish() }
        }
    }

    private fun setViewPager() {
        adapter = OnBoardingPagerAdapter(this)
        binding.viewpager.adapter = adapter
        binding.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.dotsIndicator.attachTo(binding.viewpager)
    }
    private fun setNextButton() {
        binding.btnOnboarding.setOnClickListener {
            val maxCount = adapter.itemCount
            viewModel.onClickNextButton(maxCount)
        }
    }

    private fun navigateLogin() {
        (activity as GlobalNavigation).navigateLogin()
    }

    private fun overrideBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.backPressed()
                }
            }
        )
    }

}