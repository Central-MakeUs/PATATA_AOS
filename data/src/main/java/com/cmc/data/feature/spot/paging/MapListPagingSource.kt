package com.cmc.data.feature.spot.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cmc.data.feature.spot.model.toDomain
import com.cmc.data.feature.spot.remote.SpotApiService
import com.cmc.domain.feature.spot.model.SpotWithMap
import com.cmc.domain.model.SpotCategory

class MapListPagingSource(
    private val apiService: SpotApiService,
    private val categoryId: Int,
    private val minLatitude: Double,
    private val minLongitude: Double,
    private val maxLatitude: Double,
    private val maxLongitude: Double,
    private val userLatitude: Double,
    private val userLongitude: Double,
    private val withSearch: Boolean,
    private val callback: (Int) -> Unit,
) : PagingSource<Int, SpotWithMap>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SpotWithMap> {
        return try {
            val currentPage = params.key ?: 0

            // API 호출
            val categoryIdRequest = if (categoryId == SpotCategory.ALL.id) null else categoryId
            val response = apiService.getCategorySpotsWithMapList(
                categoryIdRequest,
                minLatitude,
                minLongitude,
                maxLatitude,
                maxLongitude,
                userLatitude,
                userLongitude,
                withSearch,
                currentPage
            )

            val result = response.result?.toDomain() ?: return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )

            val nextPage = if (result.currentPage < result.totalPages - 1) result.currentPage + 1 else null
            val prevPage = if (result.currentPage == 0) null else result.currentPage - 1

            callback.invoke(result.totalCount)

            LoadResult.Page(
                data = result.items,
                prevKey = prevPage,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, SpotWithMap>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}