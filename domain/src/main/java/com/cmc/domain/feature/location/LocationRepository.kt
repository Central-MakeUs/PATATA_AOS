package com.cmc.domain.feature.location

import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    suspend fun getCurrentLocation(): Result<Location>

}