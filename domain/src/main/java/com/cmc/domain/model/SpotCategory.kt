package com.cmc.domain.model

enum class SpotCategory(val id: Int) {
    ALL(0),
    RECOMMEND(1),
    SNAP(2),
    NIGHT(3),
    EVERYDAY(4),
    NATURE(5);

    companion object {
        fun fromId(id: Int) = entries.first { it.id == id }
        fun isRecommended(id: Int) = id == RECOMMEND.id
    }
}