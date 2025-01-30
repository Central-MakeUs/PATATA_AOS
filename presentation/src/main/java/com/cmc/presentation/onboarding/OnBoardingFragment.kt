package com.cmc.presentation.onboarding

import androidx.activity.OnBackPressedCallback
import androidx.viewpager2.widget.ViewPager2
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentOnboardingBinding

class OnBoardingFragment : BaseFragment<FragmentOnboardingBinding>(R.layout.fragment_onboarding) {

    private lateinit var adapter: OnBoardingPagerAdapter

    override fun initView() {

        setViewPager()
        setNextButton(count = adapter.itemCount)
        overrideBackPressed()

    }

    private fun setViewPager() {
        adapter = OnBoardingPagerAdapter(this)
        binding.viewpager.adapter = adapter
        binding.viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.dotsIndicator.setViewPager2(binding.viewpager)
    }

    private fun setNextButton(count: Int) {
        binding.btnOnboarding.setOnClickListener {
            val currentItem = binding.viewpager.currentItem
            if (currentItem < count - 1) {
                binding.viewpager.currentItem = currentItem + 1
            } else {
                (activity as GlobalNavigation).navigateLogin()
            }
        }
    }

    private fun overrideBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val currentItem = binding.viewpager.currentItem
                    if (binding.viewpager.currentItem != 0) {
                        binding.viewpager.currentItem = currentItem - 1
                    } else {
                        finish()
                    }
                }
            }
        )
    }

    override fun initObserving() {

    }

}