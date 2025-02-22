package com.cmc.data.feature.spot.model

import com.cmc.domain.feature.spot.model.EditSpotResponse
import com.google.gson.annotations.SerializedName

data class EditSpotResponseDto(
    @SerializedName("spotId")
    val spotId: Int,
    @SerializedName("spotName")
    val spotName: String,
    @SerializedName("spotAddress")
    val spotAddress: String,
    @SerializedName("spotAddressDetail")
    val spotAddressDetail: String?,
    @SerializedName("spotDescription")
    val spotDesc: String,
    @SerializedName("categoryName")
    val categoryName: String,
)

fun EditSpotResponseDto.toDomain(): EditSpotResponse {
    return EditSpotResponse(
        spotId = spotId,
        spotName = spotName,
        spotAddress = spotAddress,
        spotAddressDetail = spotAddressDetail,
        spotDesc = spotDesc,
        categoryName = categoryName,
    )
}