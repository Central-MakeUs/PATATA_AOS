package com.cmc.presentation.map.model

data class CheckSpotRegistrationResponse(
    val spotId: Int,
    val spotName: String,
    val latitude: Double,
    val longitude: Double,
)