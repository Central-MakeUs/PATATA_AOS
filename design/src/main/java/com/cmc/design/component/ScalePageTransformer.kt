package com.cmc.design.component

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class ScalePageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val scaleFactor = 0.05f
        val minScale = 0.95f
        val scale = (1 - kotlin.math.abs(position) * scaleFactor).coerceAtLeast(minScale)

        // 확대/축소 효과
        page.scaleX = scale
        page.scaleY = scale

        // 간격 조정
        val offset = position * page.width * 0.12f
        page.translationX = -offset
    }
}