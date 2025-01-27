package com.cmc.domain.model

import java.util.Date

data class Spot(
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
