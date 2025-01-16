package com.cmc.design.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.cmc.design.R

class SpotViewPager @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val viewPager: ViewPager2

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_spot_viewpager, this, true)
        viewPager = view.findViewById(R.id.vp_inner)

        viewPager.offscreenPageLimit = 3
        val decoration = PageDecoration(ITEM_MARGIN)

        viewPager.addItemDecoration(decoration)
        viewPager.setPageTransformer(ScalePageTransformer())
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        viewPager.adapter = adapter
    }

    companion object {
        const val ITEM_MARGIN = 16  // dp로 저장
    }
}