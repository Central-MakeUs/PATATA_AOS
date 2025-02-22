package com.cmc.presentation.spot.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.base.exception.ApiException
import com.cmc.domain.base.exception.AppInternalException
import com.cmc.domain.constants.ImageUploadPolicy
import com.cmc.domain.feature.spot.usecase.EditSpotUseCase
import com.cmc.domain.feature.spot.usecase.GetSpotDetailUseCase
import com.cmc.domain.model.ImageMetadata
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.util.toStringImageMetaDataList
import com.cmc.presentation.util.toUriImageMetaDataList
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
class EditSpotViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val editSpotUseCase: EditSpotUseCase,
    private val getSpotDetailUseCase: GetSpotDetailUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(EditSpotState())
    val state: StateFlow<EditSpotState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<EditSpotSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    init {
        observeStateChanges()
    }

    fun setSelectLocationResult(address: String, latitude: Double, longitude: Double) {
        _state.update {
            it.copy(address = address, latitude = latitude, longitude = longitude)
        }
    }

    fun fetchSpotDetail(spotId: Int?, callback: (EditSpotState) -> Unit) {
        if (spotId == null) {
            sendSideEffect(EditSpotSideEffect.Finish)
            return
        }
        viewModelScope.launch {
            getSpotDetailUseCase.invoke(spotId)
                .onSuccess { spot ->
                    _state.update {
                        val result = spot.images.toStringImageMetaDataList(context)
                        val images = result.mapNotNull { image -> image.getOrNull() }

                        val newState = it.copy(
                            isInitialized = true,
                            spotId = spotId,
                            spotName = spot.spotName,
                            latitude = spot.latitude,
                            longitude = spot.longitude,
                            address = spot.address,
                            addressDetail = spot.addressDetail,
                            description = spot.description,
                            selectedCategory = SpotCategory.fromId(spot.categoryId),
                            selectedImages = images,
                            tags = spot.tags,
                        )
                        callback.invoke(newState)
                        newState
                    }
                }
                .onFailure { e ->
                    when (e) {
                        is ApiException.NotFound, is AppInternalException.PermissionDenied -> { sendSideEffect(EditSpotSideEffect.Finish) }
                        else -> {
                            sendSideEffect(EditSpotSideEffect.ShowSnackbar(e.message.toString()))
                        }
                    }

                }
        }
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
            _sideEffect.emit(EditSpotSideEffect.ShowCategoryPicker)
        }
    }

    fun openPhotoPicker() {
        viewModelScope.launch {
            _sideEffect.emit(EditSpotSideEffect.ShowPhotoPicker)
        }
    }

    fun onCancelButtonClicked() {
        viewModelScope.launch {
            _sideEffect.emit(EditSpotSideEffect.Finish)
        }
    }
    fun onClickEditButton() {
        viewModelScope.launch {
            state.value.let {
                editSpotUseCase.invoke(
                    spotId = it.spotId,
                    spotName = it.spotName,
                    spotDesc = it.description,
                    spotAddress = it.address,
                    spotAddressDetail = it.addressDetail,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    categoryId = it.selectedCategory!!.id,
                    tags = it.tags,
                ).onSuccess {
                    sendSideEffect(EditSpotSideEffect.Finish)
                }.onFailure { e ->
                    e.printStackTrace()
                }
            }
        }
    }
    fun onClickAddress() {
        val currentState = state.value
        sendSideEffect(EditSpotSideEffect.NavigateSelectLocation(currentState.latitude, currentState.longitude))
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
        val imageResults = images.toUriImageMetaDataList(context)
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
                _sideEffect.emit(EditSpotSideEffect.ShowSnackbar("최대 ${MAX_TAG_COUNT}개의 태그만 추가할 수 있습니다."))
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
    private fun checkFormValid(state: EditSpotState): Boolean {
        return state.spotName.isNotBlank() &&
                state.description.isNotBlank() &&
                state.selectedCategory != null &&
                state.selectedImages.isNotEmpty()
    }

    private fun sendSideEffect(effect: EditSpotSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class EditSpotState(
        val isInitialized: Boolean = false,
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val spotId: Int = 0,
        val spotName: String = "",
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val address: String = "",
        val addressDetail: String? = null,
        val description: String = "",
        val selectedCategory: SpotCategory? = null,
        val selectedImages: List<ImageMetadata> = emptyList(),
        val tags: List<String> = emptyList(),
        val isRegisterEnabled: Boolean = false,  // 등록 버튼 활성화 여부
    )

    sealed class EditSpotSideEffect {
        data object Finish: EditSpotSideEffect()
        data object ShowCategoryPicker : EditSpotSideEffect()
        data object ShowPhotoPicker : EditSpotSideEffect()
        data class NavigateSelectLocation(val latitude: Double, val longitude: Double): EditSpotSideEffect()
        data class ShowSnackbar(val message: String) : EditSpotSideEffect()
    }

    companion object {
        const val MAX_TAG_COUNT = 2
    }
}