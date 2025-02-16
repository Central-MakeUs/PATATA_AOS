package com.cmc.presentation.search.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.cmc.common.constants.PrimitiveValues.Location.DEFAULT_LATITUDE
import com.cmc.common.constants.PrimitiveValues.Location.DEFAULT_LONGITUDE
import com.cmc.domain.feature.location.GetCurrentLocationUseCase
import com.cmc.domain.feature.location.Location
import com.cmc.domain.feature.spot.usecase.GetSearchSpotsUseCase
import com.cmc.domain.feature.spot.usecase.ToggleSpotScrapUseCase
import com.cmc.domain.model.CategorySortType
import com.cmc.presentation.search.adapter.SpotThumbnailAdapter
import com.cmc.presentation.search.model.SpotWithDistanceUiModel
import com.cmc.presentation.search.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val getSearchSpotsUseCase: GetSearchSpotsUseCase,
    private val toggleSpotScrapUseCase: ToggleSpotScrapUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SearchSideEffect>()
    val sideEffect: SharedFlow<SearchSideEffect> = _sideEffect.asSharedFlow()

    init {
        observeStateChange()
    }

    // 검색 수행
    fun performSearch(query: String) {
        if (query.isBlank()) {
            _state.update { it.copy(query = query, searchStatus = SearchStatus.IDLE) }
            return
        }

        _state.update {
            it.copy(query = query, searchStatus = SearchStatus.LOADING)
        }
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

    fun getSortType(): CategorySortType = state.value.sortType

    fun onClickSearchSortButton() {
        sendSideEffect(SearchSideEffect.ShowSortDialog)
    }
    fun onClickSortByDistance() {
        _state.update {
            it.copy(sortType = CategorySortType.DISTANCE)
        }
    }
    fun onClickSortByRecommend() {
        _state.update {
            it.copy(sortType = CategorySortType.RECOMMEND)
        }
    }
    fun onClickSpotScrapButton(spotId: Int) {
        viewModelScope.launch {
            toggleSpotScrapUseCase.invoke(spotId)
                .onSuccess {
                    val newPagingData = state.value.results.map { spot ->
                        if (spot.spotId == spotId) {
                            val isScraped = spot.isScraped.not()
                            spot.copy(
                                isScraped = isScraped,
                                scrapCount = spot.scrapCount + if (isScraped) 1 else - 1
                            )
                        } else {
                            spot
                        }
                    }

                    _state.update {
                        it.copy(
                            results = newPagingData
                        )
                    }
                }
        }
    }
    fun onClickSpotImage(spotId: Int) {
        sendSideEffect(SearchSideEffect.NavigateSpotDetail(spotId))
    }

    fun clickToastBtn(message: String) {
        sendSideEffect(SearchSideEffect.ShowToast(message))
    }

    private fun sendSideEffect(effect: SearchSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    private fun observeStateChange() {
        viewModelScope.launch {
            combine(
                _state.map { it.query }.distinctUntilChanged(),
                _state.map { it.sortType }.distinctUntilChanged()
            ) { query, sortType ->
                query to sortType
            }
            .filter { (query, _) -> query.isNotEmpty() }
            .collectLatest { (query, sortType) ->
                getCurrentLocationUseCase.invoke()
                    .onSuccess { location ->
                        updateSearchSpots(query, location, sortType)
                    }
                    .onFailure { e ->
                        when (e) {
                            is SecurityException -> {
                                val location = Location(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
                                updateSearchSpots(query, location, sortType)
                            }
                        }
                    }
            }
        }
    }

    private suspend fun updateSearchSpots(
        keyword: String,
        location: Location,
        sortType: CategorySortType
    ) {
        getSearchSpotsUseCase.invoke(
            keyword = keyword,
            latitude = location.latitude,
            longitude = location.longitude,
            sortBy = sortType.name,
            totalCountCallBack = { count ->
                _state.update {
                    it.copy(spotCount = count)
                }
            })
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
                        results = pagingData.map { data -> data.toUiModel() },
                        searchStatus = SearchStatus.SUCCESS
                    )
                }
            }
    }


    data class SearchState(
        val query: String = "",
        val sortType: CategorySortType = CategorySortType.getDefault(),
        val results: PagingData<SpotWithDistanceUiModel> = PagingData.empty(),
        val spotCount: Int = 0,
        val errorMessage: String? = null,
        val searchStatus: SearchStatus = SearchStatus.IDLE
    )

    enum class SearchStatus {
        IDLE,
        LOADING,
        SUCCESS,
        ERROR,
        EMPTY,
        LOADED,
    }

    sealed class SearchSideEffect {
        data class NavigateSpotDetail(val spotId: Int): SearchSideEffect()
        data class ShowToast(val message: String) : SearchSideEffect()
        data object ShowSortDialog : SearchSideEffect()
    }

}
