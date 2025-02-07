package com.cmc.domain.model

enum class CategorySortType {
    RECOMMEND,
    DISTANCE;
    companion object {
        fun getDefault(): CategorySortType = RECOMMEND
    }
}