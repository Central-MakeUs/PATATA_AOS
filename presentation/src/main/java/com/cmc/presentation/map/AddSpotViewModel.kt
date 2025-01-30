package com.cmc.presentation.map

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.model.SpotCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddSpotViewModel @Inject constructor(): ViewModel() {

    private val _state = MutableStateFlow(AddSpotState())
    val state: StateFlow<AddSpotState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<AddSpotSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    init {
        observeStateChanges()
    }

    fun updateTitle(title: String) {
        _state.update { it.copy(title = title) }
    }

    fun updateLocation(location: String) {
        _state.update { it.copy(detailedLocation = location) }
    }

    fun updateDescription(description: String) {
        _state.update { it.copy(description = description) }
    }

    fun selectCategory(category: SpotCategory) {
        _state.update { it.copy(selectedCategory = category) }
    }

    fun openCategoryPicker() {
        viewModelScope.launch {
            _sideEffect.emit(AddSpotSideEffect.ShowCategoryPicker)
        }
    }

    fun openPhotoPicker() {
        viewModelScope.launch {
            _sideEffect.emit(AddSpotSideEffect.ShowPhotoPicker)
        }
    }

    fun onCancelButtonClicked() {
        viewModelScope.launch {
            _sideEffect.emit(AddSpotSideEffect.NavigateToAroundMe)
        }
    }

    fun updateSelectedImages(images: List<Uri>) {
        val updatedList = (_state.value.selectedImages + images).distinct()

        // 3장 초과 여부 확인
        val finalList = if (updatedList.size > MAX_IMAGE_COUNT) {
            viewModelScope.launch {
                _sideEffect.emit(AddSpotSideEffect.ShowSnackbar("이미지는 최대 ${MAX_IMAGE_COUNT}장까지 업로드가 가능합니다."))
            }
            updatedList.subList(0, MAX_IMAGE_COUNT)
        } else {
            updatedList
        }

        _state.update {
            it.copy(selectedImages = finalList)
        }
    }

    fun removeSelectedImage(image: Uri) {
        _state.update { it.copy(selectedImages = it.selectedImages - image) }
    }

    fun addTag(tag: String) {
        if (_state.value.tags.size >= MAX_TAG_COUNT) {
            viewModelScope.launch {
                _sideEffect.emit(AddSpotSideEffect.ShowSnackbar("최대 ${MAX_TAG_COUNT}개의 태그만 추가할 수 있습니다."))
            }
            return
        }
        _state.update { it.copy(tags = it.tags + tag) }
    }

    fun removeTag(tag: String) {
        _state.update { it.copy(tags = it.tags - tag) }
    }

    private fun observeStateChanges() {
        viewModelScope.launch {
            _state.collectLatest { currentState ->
                val isFormValid = checkFormValid(currentState)
                if (currentState.isRegisterEnabled != isFormValid) {
                    _state.update { it.copy(isRegisterEnabled = isFormValid) }
                }
            }
        }
    }

    // 필수 입력 필드 검사
    private fun checkFormValid(state: AddSpotState): Boolean {
        return state.title.isNotBlank() &&
                state.detailedLocation.isNotBlank() &&
                state.description.isNotBlank() &&
                state.selectedCategory != null &&
                state.selectedImages.isNotEmpty() &&
                state.tags.isNotEmpty()
    }

    data class AddSpotState(
        val title: String = "",
        val detailedLocation: String = "",
        val description: String = "",
        val selectedCategory: SpotCategory? = null,
        val selectedImages: List<Uri> = emptyList(),
        val tags: List<String> = emptyList(),
        val isRegisterEnabled: Boolean = false,  // 등록 버튼 활성화 여부
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    sealed class AddSpotSideEffect {
        data object ShowCategoryPicker : AddSpotSideEffect()
        data object ShowPhotoPicker : AddSpotSideEffect()
        data object NavigateToAroundMe : AddSpotSideEffect()
        data object NavigateToSpotAddedSuccess : AddSpotSideEffect()
        data class ShowSnackbar(val message: String) : AddSpotSideEffect()
    }

    companion object {
        const val MAX_TAG_COUNT = 2
        const val MAX_IMAGE_COUNT = 3
    }
}