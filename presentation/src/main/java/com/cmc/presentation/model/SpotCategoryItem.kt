package com.cmc.presentation.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R

data class SpotCategoryItem(
    private val categoryItem: SpotCategory
) {

    @StringRes fun getName(): Int {
        return when (this.categoryItem) {
            SpotCategory.ALL -> R.string.category_all
            SpotCategory.RECOMMEND -> R.string.category_recommend
            SpotCategory.SNAP -> R.string.category_snap
            SpotCategory.NIGHT -> R.string.category_night_view
            SpotCategory.EVERYDAY -> R.string.category_everyday_life
            SpotCategory.NATURE -> R.string.category_nature
        }
    }

    @DrawableRes fun getIcon(): Int? {
        return when (this.categoryItem) {
            SpotCategory.ALL -> null
            SpotCategory.RECOMMEND -> com.cmc.design.R.drawable.ic_category_recommend
            SpotCategory.SNAP -> com.cmc.design.R.drawable.ic_category_snap
            SpotCategory.NIGHT -> com.cmc.design.R.drawable.ic_category_night_view
            SpotCategory.EVERYDAY -> com.cmc.design.R.drawable.ic_category_everyday_life
            SpotCategory.NATURE -> com.cmc.design.R.drawable.ic_category_nature
        }
    }

    @DrawableRes fun getMarkerIcon(): Int {
        return when (this.categoryItem) {
            SpotCategory.RECOMMEND -> R.drawable.ic_pin_recommend
            SpotCategory.SNAP -> R.drawable.ic_pin_snap
            SpotCategory.NIGHT -> R.drawable.ic_pin_night
            SpotCategory.EVERYDAY -> R.drawable.ic_pin_everyday
            SpotCategory.NATURE -> R.drawable.ic_pin_nature
            else -> throw IllegalStateException("Unexpected category: ${this.categoryItem}")
        }
    }
}