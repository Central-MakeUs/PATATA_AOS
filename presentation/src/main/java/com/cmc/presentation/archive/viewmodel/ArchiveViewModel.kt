package com.cmc.presentation.archive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.design.component.PatataAppBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.cmc.design.component.PatataAppBar.FooterType

@HiltViewModel
class ArchiveViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ArchiveState())
    val state: StateFlow<ArchiveState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<ArchiveSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()


    fun togglePhotoSelection(imageId: Int) {
        _state.update {
            val updatedSelection = it.selectedImages.toMutableSet().apply {
                if (contains(imageId)) remove(imageId) else add(imageId)
            }
            it.copy(selectedImages = updatedSelection)
        }
    }

    fun onClickSelectButton() {
        _state.update {
            it.copy(footerType = FooterType.DELETE)
        }
    }

    fun onClickDeleteButton() {
        viewModelScope.launch {
            _sideEffect.emit(ArchiveSideEffect.ShowDeleteImageDialog(state.value.selectedImages.toList()))
        }
    }
    fun onClickSpotImage(spotId: Int) {
        sendSideEffect(ArchiveSideEffect.NavigateSpotDetail(spotId))
    }

    fun tempDeleteImages(selectedImages: List<Int>) {
        _state.update {
            val newImages = it.images.filter { v -> selectedImages.contains(v.first).not() }
            it.copy(
                footerType = if (newImages.isEmpty()) FooterType.NONE else FooterType.SELECT,
                selectedImages = emptySet(),
                images = newImages
            )
        }
    }

    fun onDeleteCancelled() {
        _state.update {
            it.copy(
                footerType = FooterType.SELECT,
                selectedImages = emptySet(),
            )
        }
    }

    private fun sendSideEffect(effect: ArchiveSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }


    data class ArchiveState(
        val footerType: PatataAppBar.FooterType = PatataAppBar.FooterType.SELECT,
        val selectedImages: Set<Int> = emptySet(),
        val images: List<Pair<Int, String>> = emptyList()
    )

    sealed class ArchiveSideEffect {
        data class ShowDeleteImageDialog(val selectedImages: List<Int>) : ArchiveSideEffect()
        data class ShowSnackbar(val message: String) : ArchiveSideEffect()
        data class NavigateSpotDetail(val spotId: Int) : ArchiveSideEffect()
    }
}