package com.cmc.presentation.search
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay

class SpotPagingSource(
    private val query: String,
    private val sortType: SearchViewModel.SortType
) : PagingSource<Int, SearchViewModel.TempSpotResult>() {

    private val dummyData = listOf(
        SearchViewModel.TempSpotResult("서울숲 은행나무길", 5.3, 117, 117, "https://example.com/image1.jpg", true),
        SearchViewModel.TempSpotResult("마포대교 북단 중앙로", 8.7, 21, 24, "https://example.com/image2.jpg", false),
        SearchViewModel.TempSpotResult("매봉산 꼭대기", 9.8, 57, 12, "https://example.com/image3.jpg", true),
        SearchViewModel.TempSpotResult("덕수궁 벚꽃나무 앞", 11.1, 21, 4, "https://example.com/image4.jpg", false),
        SearchViewModel.TempSpotResult("이촌 한강공원 철교 앞", 12.3, 30, 18, "https://example.com/image5.jpg", true),
        SearchViewModel.TempSpotResult("효사장공원 3번 쉼터", 13.5, 14, 9, "https://example.com/image6.jpg", false),
        SearchViewModel.TempSpotResult("남산타워 야경", 6.5, 98, 45, "https://example.com/image7.jpg", true),
        SearchViewModel.TempSpotResult("북촌 한옥마을", 7.2, 84, 32, "https://example.com/image8.jpg", false),
        SearchViewModel.TempSpotResult("경복궁 광장", 5.9, 63, 22, "https://example.com/image9.jpg", true),
        SearchViewModel.TempSpotResult("한강 뚝섬유원지", 10.0, 45, 15, "https://example.com/image10.jpg", false),
        SearchViewModel.TempSpotResult("청계천 야경", 8.0, 74, 29, "https://example.com/image11.jpg", true),
        SearchViewModel.TempSpotResult("홍대 걷고싶은 거리", 9.4, 52, 18, "https://example.com/image12.jpg", false),
        SearchViewModel.TempSpotResult("롯데타워 전망대", 14.0, 120, 85, "https://example.com/image13.jpg", true),
        SearchViewModel.TempSpotResult("삼청동 카페거리", 6.3, 36, 14, "https://example.com/image14.jpg", false),
        SearchViewModel.TempSpotResult("인사동 문화거리", 7.8, 95, 44, "https://example.com/image15.jpg", true),
        SearchViewModel.TempSpotResult("올림픽공원 장미광장", 12.0, 33, 17, "https://example.com/image16.jpg", false),
        SearchViewModel.TempSpotResult("광장시장", 4.8, 150, 100, "https://example.com/image17.jpg", true),
        SearchViewModel.TempSpotResult("DDP 디자인광장", 11.2, 79, 42, "https://example.com/image18.jpg", false),
        SearchViewModel.TempSpotResult("서촌 골목길", 9.1, 67, 30, "https://example.com/image19.jpg", true),
        SearchViewModel.TempSpotResult("남대문시장", 5.2, 88, 41, "https://example.com/image20.jpg", false),
        SearchViewModel.TempSpotResult("성수동 카페거리", 8.5, 92, 50, "https://example.com/image21.jpg", true),
        SearchViewModel.TempSpotResult("경의선 숲길", 10.8, 55, 20, "https://example.com/image22.jpg", false),
        SearchViewModel.TempSpotResult("양재천 벚꽃길", 13.2, 44, 19, "https://example.com/image23.jpg", true),
        SearchViewModel.TempSpotResult("한옥마을 뷰포인트", 7.7, 99, 53, "https://example.com/image24.jpg", false),
        SearchViewModel.TempSpotResult("한강시민공원 반포지구", 9.6, 112, 73, "https://example.com/image25.jpg", true),
        SearchViewModel.TempSpotResult("잠실 한강공원 자전거도로", 6.8, 421, 200, "https://example.com/image5.jpg", true),
        SearchViewModel.TempSpotResult("여의도 한강공원 벚꽃길", 11.3, 520, 310, "https://example.com/image6.jpg", false)
    )


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchViewModel.TempSpotResult> {
        return try {
            val page = params.key ?: 1
            delay(1000) // 네트워크 요청 시뮬레이션

            // 검색어 필터링
            val filteredData = dummyData.filter { it.title.contains(query, ignoreCase = true) }

            if (filteredData.isEmpty()) {
                // 빈 결과가 반환된 경우
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }

            // 정렬 적용
            val sortedData = when (sortType) {
                SearchViewModel.SortType.DISTANCE -> filteredData.sortedBy { it.distance }
                SearchViewModel.SortType.ARCHIVE -> filteredData.sortedByDescending { it.scraps }
            }

            val startIndex = (page - 1) * params.loadSize
            val endIndex = (startIndex + params.loadSize).coerceAtMost(sortedData.size)

            LoadResult.Page(
                data = sortedData.subList(startIndex, endIndex),
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (endIndex < sortedData.size) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, SearchViewModel.TempSpotResult>): Int? {
        return state.anchorPosition
    }
}
