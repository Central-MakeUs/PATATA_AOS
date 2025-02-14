package com.cmc.data.feature.spot.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cmc.data.feature.spot.model.toDomain
import com.cmc.data.feature.spot.remote.SpotApiService
import com.cmc.domain.feature.spot.model.SpotWithDistance

class SearchSpotPagingSource(
    private val apiService: SpotApiService,
    private val keyword: String,
    private val latitude: Double,
    private val longitude: Double,
    private val sortBy: String
) : PagingSource<Int, SpotWithDistance>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SpotWithDistance> {
        return try {
            val currentPage = params.key ?: 0

            // API 호출
            val response = apiService.getSearchSpots(keyword, currentPage, latitude, longitude, sortBy)

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

    override fun getRefreshKey(state: PagingState<Int, SpotWithDistance>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}