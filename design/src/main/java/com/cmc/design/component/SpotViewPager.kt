package com.cmc.design.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.cmc.design.R
import kotlin.math.ceil

class SpotViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val viewPager: ViewPager2

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_spot_viewpager, this, true)
        viewPager = view.findViewById(R.id.vp_inner)

        viewPager.offscreenPageLimit = 3
        val decoration = PageDecoration(PREV_WIDTH + ITEM_MARGIN)

        viewPager.addItemDecoration(decoration)
        viewPager.setPageTransformer(ScalePageTransformer(PREV_WIDTH, ITEM_MARGIN))
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        viewPager.adapter = adapter
        post {
//            viewPager.setCurrentItem(Int.MAX_VALUE / 2, false)
            viewPager.currentItem = Int.MAX_VALUE / 2
        }
    }

    companion object {
        const val PREV_WIDTH = 120
        const val ITEM_MARGIN = 16
    }
}