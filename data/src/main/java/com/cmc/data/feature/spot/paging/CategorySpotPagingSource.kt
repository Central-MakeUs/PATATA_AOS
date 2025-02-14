package com.cmc.data.feature.spot.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cmc.data.feature.spot.model.toDomain
import com.cmc.data.feature.spot.remote.SpotApiService
import com.cmc.domain.feature.spot.model.SpotWithStatus
import com.cmc.domain.model.SpotCategory

class CategorySpotPagingSource(
    private val apiService: SpotApiService,
    private val categoryId: Int,
    private val latitude: Double,
    private val longitude: Double,
    private val sortBy: String
) : PagingSource<Int, SpotWithStatus>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SpotWithStatus> {
        return try {
            val currentPage = params.key ?: 0

            // API 호출
            val categoryIdRequest = if (categoryId == SpotCategory.ALL.id) null else categoryId
            val response = apiService.getCategorySpots(categoryIdRequest, currentPage, latitude, longitude, sortBy)

            val result = response.result?.toDomain() ?: return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )

            val nextPage = if (result.currentPage < result.totalPages - 1) result.currentPage + 1 else null
            val prevPage = if (result.currentPage == 0) null else result.currentPage - 1

            LoadResult.Page(
                data = result.items,
                prevKey = prevPage,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, SpotWithStatus>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}