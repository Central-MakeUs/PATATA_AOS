package com.cmc.design.component

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.cmc.design.R
import com.cmc.design.databinding.TabCustomBinding
import com.cmc.design.databinding.ViewCustomTabLayoutBinding
import com.cmc.design.util.Util.dp
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab

class CustomTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: ViewCustomTabLayoutBinding =
        ViewCustomTabLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    private val tabLayout: TabLayout = binding.tabLayout
    private val tabList: MutableList<Pair<String, Drawable?>> = mutableListOf()

    private var textColor: Int = R.color.text_info
    private var tabBackground: Int = R.drawable.selector_tab
    private var textAppearance: Int = R.style.caption_medium

    init {
        initAttributes(context, attrs)

    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.CustomTabLayout, 0, 0).apply {
            try {
                val indicatorColor = getColor(R.styleable.CustomTabLayout_customTabIndicatorColor, context.getColor(R.color.transparent))
                val selectedTextColor = getColor(R.styleable.CustomTabLayout_customTabSelectedTextColor, context.getColor(R.color.black))
                val unselectedTextColor = getColor(R.styleable.CustomTabLayout_customTabUnselectedTextColor, context.getColor(R.color.text_info))
                tabBackground = getResourceId(R.styleable.CustomTabLayout_customTabBackground, R.drawable.selector_tab)
                val selectedTextAppearance = getResourceId(R.styleable.CustomTabLayout_customTabSelectedTextAppearance, R.style.caption_medium)
                val unselectedTextAppearance = getResourceId(R.styleable.CustomTabLayout_customTabUnselectedTextAppearance, R.style.caption_medium)

                // 속성 적용
                tabLayout.setSelectedTabIndicatorColor(indicatorColor)
                tabLayout.tabMode = TabLayout.MODE_SCROLLABLE

                tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: Tab?) {
                        tab?.customView?.findViewById<TextView>(R.id.tab_text)?.apply {
                            setTextAppearance(selectedTextAppearance)
                            setTextColor(selectedTextColor)
                        }
                    }

                    override fun onTabUnselected(tab: Tab?) {
                        tab?.customView?.findViewById<TextView>(R.id.tab_text)?.apply {
                            setTextAppearance(unselectedTextAppearance)
                            setTextColor(unselectedTextColor)
                        }
                    }

                    override fun onTabReselected(tab: Tab?) {}
                })
            } finally {
                recycle()
            }
        }
    }

    fun setTabList(tabs: List<Pair<String, Drawable?>>) {
        tabList.clear()
        tabList.addAll(tabs)

        addTabs()
    }

    private fun addTabs() {
        tabLayout.removeAllTabs()
        tabList.forEach {
            tabLayout.addTab(
                newTabs(
                    background = tabBackground,
                    text = it.first,
                    textColor = textColor,
                    textAppearance = textAppearance,
                    icon = it.second
                )
            )
        }
    }

    private fun newTabs(
        @DrawableRes background: Int,
        text: String,
        @ColorRes textColor: Int,
        @StyleRes textAppearance: Int,
        icon: Drawable?,
    ): Tab {
        return tabLayout.newTab().apply {
            setCustomView(
                TabCustomBinding.inflate(LayoutInflater.from(context)).apply {
                    val layoutParams = tabText.layoutParams as ViewGroup.MarginLayoutParams

                    layoutRoot.background = AppCompatResources.getDrawable(context, background)
                    tabText.setTextColor(context.getColor(textColor))
                    tabText.text = text
                    tabText.setTextAppearance(textAppearance)

                    // Icon이 없다면 좌우 Padding 16dp이 되도록 추가 Margin 부여
                    icon?.let {
                        layoutParams.setMargins(3.dp,0,0,0)
                        tabIcon.isVisible = true
                        tabIcon.setImageDrawable(it)
                    } ?: run {
                        layoutParams.setMargins(4.dp,0,4.dp,0)
                    }

                }.root
            )
        }
    }
}
