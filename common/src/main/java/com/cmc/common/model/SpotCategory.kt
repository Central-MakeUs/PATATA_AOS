package com.cmc.common.model

import androidx.annotation.StringRes

enum class SpotCategory(val id: Int, @StringRes val stringId: Int, @StringRes val resId: Int?) {
    ALL(0, com.cmc.design.R.string.category_all, null),
    RECOMMEND(1, com.cmc.design.R.string.category_recommend, com.cmc.design.R.drawable.ic_category_recommend),
    SNAP(2, com.cmc.design.R.string.category_snap, com.cmc.design.R.drawable.ic_category_snap),
    Night(3, com.cmc.design.R.string.category_night_view, com.cmc.design.R.drawable.ic_category_night_view),
    EVERYDAY(4, com.cmc.design.R.string.category_everyday_life, com.cmc.design.R.drawable.ic_category_everyday_life),
    NATURE(5,com.cmc.design.R.string.category_nature, com.cmc.design.R.drawable.ic_category_nature)
}