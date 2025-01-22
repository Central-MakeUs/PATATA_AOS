package com.cmc.presentation.search
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {

    data class SearchState(
        val query: String = "",
        val sortType: SortType = SortType.DISTANCE,
        val results: PagingData<TempSpotResult> = PagingData.empty(),
        val errorMessage: String? = null,
        val searchStatus: SearchStatus = SearchStatus.IDLE
    )

    data class TempSpotResult(
        val title: String,
        val distance: Double,  // 거리 정렬을 위해 Double 타입
        val likes: Int,
        val scraps: Int,
        val imageUrl: String,
        val isBookmarked: Boolean
    )

    enum class SearchStatus {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR,
        EMPTY,
        LOADED,
    }

    enum class SortType(val text: String) {
        DISTANCE("거리순"),
        RECOMMEND("추천순")
    }

    sealed class SearchSideEffect {
        data class ShowToast(val message: String) : SearchSideEffect()
    }

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SearchSideEffect>()
    val sideEffect: SharedFlow<SearchSideEffect> = _sideEffect.asSharedFlow()

    // 검색 수행
    fun performSearch(query: String) {
        val sortType = state.value.sortType
        if (query.isBlank()) {
            _state.update { it.copy(query = query, searchStatus = SearchStatus.IDLE) }
            return
        }

        _state.update {
            it.copy(query = query, sortType = sortType, searchStatus = SearchStatus.LOADING)
        }

        viewModelScope.launch {
            getSearchResults(query, sortType)
                .cachedIn(viewModelScope)
                .catch {
                    _state.update { it.copy(errorMessage = "오류 발생", searchStatus = SearchStatus.EMPTY) }
                }
                .distinctUntilChanged()
                .onStart {
                    _state.update {
                        it.copy(
                            searchStatus = SearchStatus.LOADING
                        )
                    }
                }
                .collectLatest { pagingData ->
                    _state.update {
                        it.copy(
                            results = pagingData,
                            searchStatus = SearchStatus.SUCCESS
                        )
                    }
                }
        }
    }

    private fun getSearchResults(query: String, sortType: SortType): Flow<PagingData<TempSpotResult>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false, prefetchDistance = 2),
            pagingSourceFactory = { SpotPagingSource(query, sortType) }
        ).flow
    }

    /**
     * paging adpater의 state를 viewModel에서 관리하도록 설정
     * */
    fun setPageAdapterLoadStateListener(adapter: SpotThumbnailAdapter) {
        adapter.addLoadStateListener { loadState ->
            val isError = loadState.refresh is LoadState.Error || loadState.append is LoadState.Error
            val isLoading = loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading
            val isNothing =
                (loadState.refresh is LoadState.NotLoading || loadState.append is LoadState.NotLoading) && adapter.itemCount == 0

            val searchStatus = when {
                isLoading -> SearchStatus.LOADING
                isError -> SearchStatus.ERROR
                isNothing -> SearchStatus.EMPTY
                else -> SearchStatus.LOADED
            }

            viewModelScope.launch {
                _state.emit(
                    state.value.copy(
                        searchStatus = searchStatus
                    )
                )
            }
        }
    }

    fun getSortType(): SortType = state.value.sortType

    fun setSortType(sortType: SortType) {
        viewModelScope.launch {
            _state.emit(
                state.value.copy(
                    sortType = sortType
                )
            )
        }
    }

    fun clickToastBtn(message: String) {
        sendSideEffect(SearchSideEffect.ShowToast(message))
    }

    private fun sendSideEffect(effect: SearchSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }
}
