package com.cmc.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(

): ViewModel() {

    data class SearchState(
        val query: String = "",
        val results: List<TempSpotResult> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val searchStatus: SearchStatus = SearchStatus.IDLE
    )

    data class TempSpotResult(
        val title: String,
        val distance: String,
        val likes: Int,
        val scraps: Int,
        val imageUrl: String,
        val isBookmarked: Boolean
    )

    enum class SearchStatus {
        IDLE,
        LOADING,
        SUCCESS,
        EMPTY
    }

    sealed class SearchSideEffect {
        data class ShowToast(val message: String) : SearchSideEffect()
    }

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    // Side effect 처리를 위한 SharedFlow (일회성 이벤트)
    private val _sideEffect = MutableSharedFlow<SearchSideEffect>()
    val sideEffect: SharedFlow<SearchSideEffect> = _sideEffect.asSharedFlow()

    // 검색 수행
    fun performSearch(query: String) {
        if (query.isBlank()) {
            _state.update { it.copy(query = query, searchStatus = SearchStatus.IDLE) }
            return
        }

        _state.update { it.copy(isLoading = true, query = query, searchStatus = SearchStatus.LOADING) }

        viewModelScope.launch {
            fetchSearchResults(query)
                .catch { exception ->
                    _state.update { it.copy(isLoading = false, errorMessage = "오류 발생", searchStatus = SearchStatus.EMPTY) }
                }
                .collect { results ->
                    _state.update {
                        if (results.isEmpty()) {
                            it.copy(results = results, isLoading = false, searchStatus = SearchStatus.EMPTY)
                        } else {
                            it.copy(results = results, isLoading = false, searchStatus = SearchStatus.SUCCESS)
                        }
                    }
                }
        }
    }

    // 검색 결과를 Flow로 반환
    private fun fetchSearchResults(query: String): Flow<List<TempSpotResult>> = flow {
        // 임의의 지연 추가 (실제 네트워크 API 대체 가능)
        kotlinx.coroutines.delay(1000)

        val mockResults = listOf(
            TempSpotResult(
                title = "서울",
                distance = "5.3Km",
                likes = 117,
                scraps = 117,
                imageUrl = "https://example.com/images/seoul_forest.jpg",
                isBookmarked = true
            ),
            TempSpotResult(
                title = "서울숲 은행나무길",
                distance = "5.3Km",
                likes = 117,
                scraps = 117,
                imageUrl = "https://example.com/images/seoul_forest.jpg",
                isBookmarked = true
            )
        )

        emit(mockResults.filter { it.title.contains(query, ignoreCase = true) })
    }

    // Side Effect 발생 처리
    private fun sendSideEffect(effect: SearchSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }
}