package com.cmc.design.component

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.cmc.design.R

class CustomIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var dotsCount: Int = 0
    private val dots = mutableListOf<View>()

    private var dotSize: Int = 20
    private var dotColor: Int = R.color.gray_50 // 기본 점 색상 (회색)
    private var dotSelectedColor: Int = R.color.blue_100

    init {
        orientation = HORIZONTAL
        gravity = android.view.Gravity.CENTER

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomIndicator, 0, 0)
            dotSize = typedArray.getDimensionPixelSize(R.styleable.CustomIndicator_dotSize, dotSize)
            dotColor = typedArray.getResourceId(R.styleable.CustomIndicator_dotColor, dotColor)
            dotSelectedColor = typedArray.getResourceId(R.styleable.CustomIndicator_dotSelectedColor, dotSelectedColor)
            typedArray.recycle()
        }
    }

    fun attachTo(viewPager: ViewPager2) {
        dotsCount = viewPager.adapter?.itemCount ?: 0
        createDots()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDots(position)
            }
        })
    }

    private fun createDots() {
        removeAllViews()
        dots.clear()

        for (i in 0 until dotsCount) {
            val dot = View(context).apply {
                background = ContextCompat.getDrawable(context, R.drawable.bg_circle) // 원형 모양 적용
                layoutParams = LayoutParams(dotSize, dotSize).apply {
                    setMargins(8, 0, 8, 0) // 점 간격 설정
                }
                // 초기 색상 설정
                backgroundTintList = ContextCompat.getColorStateList(context, dotColor)
                clipToOutline = true
                outlineProvider = ViewOutlineProvider.BACKGROUND
            }
            addView(dot)
            dots.add(dot)
        }

        if (dots.isNotEmpty()) {
            dots[0].backgroundTintList = ContextCompat.getColorStateList(context, dotSelectedColor) // 첫 번째 점 선택 상태로 변경
        }
    }

    private fun updateDots(selectedPosition: Int) {
        for (i in dots.indices) {
            dots[i].backgroundTintList = ContextCompat.getColorStateList(
                context,
                if (i == selectedPosition) dotSelectedColor // 선택된 점 색상 적용
                else dotColor // 기본 점 색상 적용
            )
        }
    }
}