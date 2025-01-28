package com.cmc.domain.location

import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    suspend fun getCurrentLocation(): Result<Location>

}