package com.cmc.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(

) : ViewModel() {

    // StateFlow로 상태 관리
    private val _selectedCategory = MutableStateFlow(SpotCategory.RECOMMEND)
    val selectedCategory: StateFlow<SpotCategory> = _selectedCategory

    // 카테고리 선택 업데이트
    fun selectCategory(category: SpotCategory) {
        _selectedCategory.value = category
    }
}

enum class SpotCategory {
    RECOMMEND, SNAP, NIGHT_VIEW, EVERYDAY_LIFE, NATURE
}