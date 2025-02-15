package com.cmc.presentation.map.model

data class MapScreenLocation(
    val minLatitude: Double,
    val minLongitude: Double,
    val maxLatitude: Double,
    val maxLongitude: Double,
) {
    companion object {
        fun getDefault(): MapScreenLocation {
            return MapScreenLocation(0.0, 0.0, 0.0, 0.0)
        }
    }
}