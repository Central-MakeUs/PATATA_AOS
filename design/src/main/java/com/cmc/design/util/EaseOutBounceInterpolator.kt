package com.cmc.design.util

    import android.view.animation.Interpolator
    import android.view.animation.PathInterpolator


class EaseOutBounceInterpolator(private val exponent: Double = 5.0) : Interpolator {
    private val pathInterpolator = PathInterpolator(0.25f, 0.1f, 0.58f, 1.0f)

    override fun getInterpolation(input: Float): Float {
        return pathInterpolator.getInterpolation(input)
    }
}