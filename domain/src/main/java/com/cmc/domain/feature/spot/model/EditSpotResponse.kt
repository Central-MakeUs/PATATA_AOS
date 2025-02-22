package com.cmc.domain.feature.spot.model

data class EditSpotResponse(
    val spotId: Int,
    val spotName: String,
    val spotAddress: String,
    val spotAddressDetail: String?,
    val spotDesc: String,
    val categoryName: String
)
