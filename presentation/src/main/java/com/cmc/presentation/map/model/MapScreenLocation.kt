package com.cmc.presentation.map.model

import com.cmc.domain.feature.location.Location

data class MapScreenLocation(
    val targetLocation: Location,
    val minLatitude: Double,
    val minLongitude: Double,
    val maxLatitude: Double,
    val maxLongitude: Double,
) {
    companion object {
        fun getDefault(): MapScreenLocation {
            return MapScreenLocation(Location(0.0, 0.0),0.0, 0.0, 0.0, 0.0)
        }
    }
}