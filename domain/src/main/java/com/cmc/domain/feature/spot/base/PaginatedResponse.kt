package com.cmc.domain.feature.spot.base

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

typealias PaginatedResponse<T> = Flow<PagingData<T>>
