package com.cmc.design.util

import android.view.animation.Interpolator
import kotlin.math.pow


class ExponentialAccelerateInterpolator(private val exponent: Double = 5.0) : Interpolator {
    override fun getInterpolation(input: Float): Float {
        return input.toDouble().pow(exponent).toFloat()
    }
}