package com.cmc.presentation.onboarding.view

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.cmc.common.base.BaseFragment
import com.cmc.design.util.Util.dp
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentOnboardingPageBinding

class OnBoardingPageFragment : BaseFragment<FragmentOnboardingPageBinding>(R.layout.fragment_onboarding_page) {

    override fun initObserving() {
    }

    override fun initView() {
        arguments?.let {
            binding.tvTitle.text = it.getString(ARG_TITLE)
            binding.tvSubTitle.text = it.getString(ARG_SUBTITLE)
            binding.ivImage.setImageResource(it.getInt(ARG_IMAGE))
            binding.layoutSpotRegist.isVisible = it.getBoolean(ARG_VISIBLE)
            setImageMargin(it.getInt(ARG_SPACE))
        }
    }

    override fun onStart() {
        super.onStart()
        animateViewUp(binding.layoutSpotRegist)
    }

    private fun setImageMargin(margin: Int) {
        val layoutParams = binding.ivImage.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(0, margin.dp, 0, 0)
        binding.ivImage.requestLayout()
    }

    private fun animateViewUp(view: LinearLayout) {
        val distance = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f , resources.displayMetrics)

        ObjectAnimator.ofFloat(view, "translationY", view.translationY, view.translationY - distance).apply {
            duration = 1500
            interpolator = LinearInterpolator()
            start()
        }
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_SUBTITLE = "subtitle"
        private const val ARG_IMAGE = "image"
        private const val ARG_VISIBLE = "bool"
        private const val ARG_SPACE = "space"

        fun newInstance(title: String, subtitle: String, imageResId: Int, isVisible: Boolean, margin: Int) =
            OnBoardingPageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_SUBTITLE, subtitle)
                    putInt(ARG_IMAGE, imageResId)
                    putBoolean(ARG_VISIBLE,isVisible)
                    putInt(ARG_SPACE,margin)
                }
            }
    }
}
