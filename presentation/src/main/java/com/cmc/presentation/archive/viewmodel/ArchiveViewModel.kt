package com.cmc.presentation.archive.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.design.component.PatataAppBar
import com.cmc.design.component.PatataAppBar.FooterType
import com.cmc.domain.feature.spot.usecase.GetScrapSpotsUseCase
import com.cmc.domain.feature.spot.usecase.ToggleSpotScrapUseCase
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.spot.model.SpotPreviewUiModel
import com.cmc.presentation.spot.model.toListUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArchiveViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getScrapSpotsUseCase: GetScrapSpotsUseCase,
    private val toggleSpotScrapUseCase: ToggleSpotScrapUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ArchiveState())
    val state: StateFlow<ArchiveState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<ArchiveSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    fun refreshArchiveScreen() {
        fetchScrapSpots()
    }
    fun clearState() {
        _state.update {
            it.copy(isLoading = true)
        }
    }
    private fun fetchScrapSpots() {
        viewModelScope.launch {
            getScrapSpotsUseCase.invoke()
                .onSuccess { images ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            images = images.toListUiModel(),
                            footerType = if (images.isEmpty()) FooterType.NONE else FooterType.SELECT
                        )
                    }
                }.onFailure {

                }
        }
    }

    fun togglePhotoSelection(spot: SpotPreviewUiModel) {
        _state.update {
            it.copy(
                images = it.images.map { image ->
                    image.copy(
                        isSelected =
                            if(image == spot) image.isSelected.not()
                            else image.isSelected
                    )
                }
            )
        }
    }

    fun onClickSelectButton() {
        _state.update {
            it.copy(footerType = FooterType.DELETE)
        }
    }

    fun onClickAppBarDeleteButton() {
        val selectedImages = state.value.images.filter { it.isSelected }.map { it.spotId }
        if (selectedImages.isEmpty()) {
            onDeleteCancelled()
        } else {
            sendSideEffect(ArchiveSideEffect.ShowDeleteImageDialog(selectedImages.size))
        }
    }
    fun onClickSpotImage(spotId: Int) {
        sendSideEffect(ArchiveSideEffect.NavigateSpotDetail(spotId))
    }
    fun onClickExploreSpotButton() {
        sendSideEffect(ArchiveSideEffect.NavigateToCategorySpots(SpotCategory.ALL.id))
    }

    fun onClickDeleteButton() {
        viewModelScope.launch {
            val selectedSpotIds= state.value.images.filter { it.isSelected }.map { it.spotId }
            toggleSpotScrapUseCase.invoke(selectedSpotIds)
                .onSuccess { spots ->
                    val deletedSpotIds = spots.map { it.spotId }
                    _state.update {
                        val newImages = it.images.filter { image ->
                            image.spotId !in deletedSpotIds
                        }.map { image ->
                            image.copy(isSelected = false)
                        }

                        it.copy(
                            footerType = if (newImages.isEmpty()) FooterType.NONE else FooterType.SELECT,
                            images = newImages
                        )
                    }

                    sendSideEffect(ArchiveSideEffect.ShowSnackbar(
                        context.getString(R.string.archive_images_deleted, deletedSpotIds.size)
                    ))
                }.onFailure { e ->
                    e.printStackTrace()
                }
        }
    }

    private fun onDeleteCancelled() {
        _state.update {
            it.copy(
                footerType = FooterType.SELECT,
                images = it.images.map {  image ->
                    image.copy(isSelected = false)
                }
            )
        }
    }


    private fun sendSideEffect(effect: ArchiveSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class ArchiveState(
        val isLoading: Boolean = true,
        val footerType: FooterType = PatataAppBar.FooterType.SELECT,
        val images: List<SpotPreviewUiModel> = emptyList()
    )

    sealed class ArchiveSideEffect {
        data class ShowDeleteImageDialog(val count: Int) : ArchiveSideEffect()
        data class ShowSnackbar(val message: String) : ArchiveSideEffect()
        data class NavigateSpotDetail(val spotId: Int) : ArchiveSideEffect()
        data class NavigateToCategorySpots(val categoryId: Int) : ArchiveSideEffect()
    }
}