package com.cmc.domain.model

enum class CategorySortType(val text: String) {
    RECOMMEND("추천순"),
    DISTANCE("거리순");
    companion object {
        fun getDefault(): CategorySortType = RECOMMEND
    }
}