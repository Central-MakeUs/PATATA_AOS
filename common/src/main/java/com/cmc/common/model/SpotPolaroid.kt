package com.cmc.common.model

data class SpotPolaroid(
    val title: String,
    val location: String,
    val imageResId: Int,
    val tags: List<String>?,
    val isArchived: Boolean,
    val isBadgeVisible: Boolean = false
)
