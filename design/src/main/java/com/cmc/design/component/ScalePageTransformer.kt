package com.cmc.design.component

import android.util.Log
import android.view.View
import androidx.viewpager2.widget.ViewPager2

class ScalePageTransformer(
    private val prevWidth: Int,
    private val itemMargin: Int,
) : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {

        val scaleFactor = SCALE_FACTOR
        val minScale = MIN_SCALE
        val scale = (1 - kotlin.math.abs(position) * scaleFactor).coerceAtLeast(minScale)

        page.scaleX = scale; page.scaleY = scale

        val offset = position * (prevWidth * 2 + itemMargin)
        page.translationX = -offset
    }

    companion object {
        const val SCALE_FACTOR = 0.05f
        const val MIN_SCALE = 0.95f
    }
}