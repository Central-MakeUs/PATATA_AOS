package com.cmc.presentation.util

import com.cmc.domain.feature.location.Location
import com.naver.maps.geometry.LatLng

fun LatLng.toLocation(): Location = Location(this.latitude, this.longitude)

fun Location.toLatLng(): LatLng = LatLng(this.latitude, this.longitude)