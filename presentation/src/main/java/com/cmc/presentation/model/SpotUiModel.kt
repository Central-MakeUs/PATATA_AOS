package com.cmc.presentation.model

import com.cmc.domain.model.Spot
import java.util.Date


data class SpotUiModel(
    val id: Int,
    val name: String,
    val description: String,
    val address: String,
    val addressDetail: String,
    val latitude: Double,
    val longitude: Double,
    val scraps: Int,
    val deleted: Boolean,
    val createdAt: Date,
    val updatedAt: Date,
    val categoryId: Int,
    val memberId: Int,
)

fun Spot.toUiModel(): SpotUiModel {
    return this.run {
        SpotUiModel(
            id = id,
            name = name,
            description = description,
            address = address,
            addressDetail = addressDetail,
            latitude = latitude,
            longitude = longitude,
            scraps = scraps,
            deleted = deleted,
            createdAt = createdAt,
            updatedAt = updatedAt,
            categoryId = categoryId,
            memberId = memberId
        )
    }
}