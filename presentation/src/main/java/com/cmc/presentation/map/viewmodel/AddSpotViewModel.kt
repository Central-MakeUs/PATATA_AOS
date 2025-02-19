package com.cmc.presentation.map.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.constants.ImageUploadPolicy
import com.cmc.domain.feature.spot.usecase.CreateSpotUseCase
import com.cmc.domain.model.ImageMetadata
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.util.toImageMetaDataList
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
class AddSpotViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val createSpotUseCase: CreateSpotUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(AddSpotState())
    val state: StateFlow<AddSpotState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<AddSpotSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    init {
        observeStateChanges()
    }

    fun updateSpotName(spotName: String) {
        _state.update { it.copy(spotName = spotName) }
    }

    fun updateLocationWithAddress(
        latitude: Double,
        longitude: Double,
        address: String,
    ) {
        _state.update {
            it.copy(
                latitude = latitude,
                longitude = longitude,
                address = address
            )
        }
    }

    fun updateAddressDetail(addressDetail: String) {
        _state.update { it.copy(addressDetail = addressDetail) }
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
    fun onClickRegisterButton() {
        viewModelScope.launch {
            state.value.let {
                createSpotUseCase.invoke(
                    spotName = it.spotName,
                    spotDesc = it.description,
                    spotAddress = it.address,
                    spotAddressDetail = it.addressDetail,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    categoryId = it.selectedCategory!!.id,
                    tags = it.tags,
                    images = it.selectedImages,
                ).onSuccess {
                    sendSideEffect(AddSpotSideEffect.NavigateToCreateSpotSuccess)
                }.onFailure { e ->
                    e.stackTrace
                }
            }
        }
    }

    // 위치 변경 된 이미지 반영
    fun setSelectedImages(images: List<ImageMetadata>) {
        _state.update {
            it.copy(selectedImages = images)
        }
    }
    fun updateSelectedImages(images: List<Uri>) {
        var imageCountErrorMessage: String? = null
        var imageSizeErrorMessage: String? = null

        // 이미지 ImageMetaData로 변환
        val imageResults = images.toImageMetaDataList(context)
        val successfulImages = imageResults.mapNotNull { it.getOrNull() }
        val failedImages = imageResults.filter { it.isFailure }
        failedImages.forEach { it.onFailure { e -> e.stackTrace } }

        // 이미지 갯수 검사
        val oldImages = _state.value.selectedImages
        val newImages = successfulImages.filterNot { newImage -> newImage.uri in _state.value.selectedImages.map { it.uri } }
        val checkedImageCountNewImages = if (oldImages.size + newImages.size > ImageUploadPolicy.MAX_IMAGE_COUNT) {
            imageCountErrorMessage = context.getString(R.string.error_image_count, ImageUploadPolicy.MAX_IMAGE_COUNT)
            oldImages + newImages.subList(0, ImageUploadPolicy.MAX_IMAGE_COUNT - oldImages.size)
        } else {
            oldImages + newImages
        }

        // 장 당 용량 검사
        val checkedSingleImageSizeNewImages = checkedImageCountNewImages.filter {
            val validateResult =  ImageUploadPolicy.isSingleImageSizeValid(it.fileSize)
            if (validateResult.not())
                imageSizeErrorMessage = context.getString(R.string.error_image_size, ImageUploadPolicy.MAX_IMAGE_SIZE_MB)
            validateResult
        }
        // 전체 용량 검사사
        var totalSize: Long = 0L
        val checkedTotalImageSizeNewImages = checkedSingleImageSizeNewImages.filter {
            totalSize += it.fileSize
            val validateResult =  ImageUploadPolicy.isTotalImageSizeValid(totalSize)
            if (validateResult.not()) {
                totalSize -= it.fileSize
                if (imageSizeErrorMessage.isNullOrEmpty())
                    imageSizeErrorMessage = context.getString(R.string.error_total_image_size, ImageUploadPolicy.MAX_TOTAL_IMAGE_SIZE_MB)
            }
            validateResult
        }

        if (successfulImages.isNotEmpty()) {
            _state.update {
                it.copy(
                    selectedImages = checkedTotalImageSizeNewImages,
                    errorMessage = if (imageSizeErrorMessage.isNullOrEmpty()) imageCountErrorMessage else imageSizeErrorMessage
                )
            }
        }
    }


    fun removeSelectedImage(image: ImageMetadata) {
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
        return state.spotName.isNotBlank() &&
                state.description.isNotBlank() &&
                state.selectedCategory != null &&
                state.selectedImages.isNotEmpty()
    }

    private fun sendSideEffect(effect: AddSpotSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class AddSpotState(
        val spotName: String = "",
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val address: String = "",
        val addressDetail: String = "",
        val description: String = "",
        val selectedCategory: SpotCategory? = null,
        val selectedImages: List<ImageMetadata> = emptyList(),
        val tags: List<String> = emptyList(),
        val isRegisterEnabled: Boolean = false,  // 등록 버튼 활성화 여부
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    sealed class AddSpotSideEffect {
        data object ShowCategoryPicker : AddSpotSideEffect()
        data object ShowPhotoPicker : AddSpotSideEffect()
        data object NavigateToAroundMe : AddSpotSideEffect()
        data object NavigateToCreateSpotSuccess : AddSpotSideEffect()
        data class ShowSnackbar(val message: String) : AddSpotSideEffect()
    }

    companion object {
        const val MAX_TAG_COUNT = 2
    }
}