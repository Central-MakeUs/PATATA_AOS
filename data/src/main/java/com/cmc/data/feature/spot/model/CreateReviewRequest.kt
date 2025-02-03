package com.cmc.data.feature.spot.model

import com.google.gson.annotations.SerializedName

data class CreateReviewRequest(
    @SerializedName("spotId")
    val spotId: Int,
    @SerializedName("reviewText")
    val reviewText: String,
)