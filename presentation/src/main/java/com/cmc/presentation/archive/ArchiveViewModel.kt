package com.cmc.presentation.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchiveViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ArchiveState())
    val state: StateFlow<ArchiveState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<ArchiveSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    fun setSelectionMode() {
        _state.update {
            it.copy(selectionMode = SelectionMode.SELECT)
        }
    }

    fun togglePhotoSelection(imageId: Int) {
        _state.update {
            val updatedSelection = it.selectedItems.toMutableSet().apply {
                if (contains(imageId)) remove(imageId) else add(imageId)
            }
            it.copy(selectedItems = updatedSelection)
        }
    }

    fun onClickDeleteButton() {
        viewModelScope.launch {
            _sideEffect.emit(ArchiveSideEffect.ShowDeleteImageDialog(state.value.selectedItems.toList()))
        }
    }

    data class ArchiveState(
        val selectionMode: SelectionMode = SelectionMode.DEFAULT,
        val selectedItems: Set<Int> = emptySet(),
        val images: List<Pair<Int, String>> = emptyList()
    )

    sealed class ArchiveSideEffect {
        data class ShowDeleteImageDialog(val images: List<Int>) : ArchiveSideEffect()
        data class ShowSnackbar(val message: String) : ArchiveSideEffect()
    }
}

enum class SelectionMode {
    DEFAULT, SELECT
}