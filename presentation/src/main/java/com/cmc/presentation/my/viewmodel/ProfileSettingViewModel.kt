package com.cmc.presentation.my.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.feature.auth.usecase.UpdateNickNameUseCase
import com.cmc.domain.feature.auth.usecase.UpdateProfileImageUseCase
import com.cmc.domain.model.ImageMetadata
import com.cmc.presentation.login.model.MemberUiModel
import com.cmc.presentation.util.uriToImageMetadata
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileSettingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val updateNickNameUseCase: UpdateNickNameUseCase,
    private val updateProfileImageUseCase: UpdateProfileImageUseCase,
): ViewModel() {

    private val _state = MutableStateFlow<ProfileSettingState>(ProfileSettingState())
    val state: StateFlow<ProfileSettingState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<ProfileSettingSideEffect>()
    val sideEffect: SharedFlow<ProfileSettingSideEffect> = _sideEffect.asSharedFlow()

    init {
        observeStateChanges()
    }

    fun initProfile(nickName: String, image: String) {
        val profile = MemberUiModel.getDefault().copy(
            nickName = nickName,
            profileImage = image
        )
        val imageMetaData = ImageMetadata.getDefault().copy(
            uri = image
        )
        _state.update {
            it.copy(
                changedNickName = profile.nickName,
                uploadImage = imageMetaData,
                profile = profile
            )
        }
    }
    fun setNickName(nickName: String) {
        _state.update {
            it.copy(
                changedNickName = nickName,
                isNickNameError = false,
            )
        }
    }
    fun setImage(uri: Uri) {
        val result = uriToImageMetadata(context, uri)
        result.onSuccess {  imageData ->
            _state.update { it.copy(uploadImage = imageData) }
        }.onFailure { e ->
            e.printStackTrace()
        }
    }
    fun onClickEditProfileImageButton() {
        sendSideEffect(ProfileSettingSideEffect.ShowPhotoPicker)
    }

    fun onClickCompleteButton() {
        viewModelScope.launch {
            val results = coroutineScope {
                listOf(
                    async { updateNickName(state.value.changedNickName) },
                    async { updateProfileImage(state.value.uploadImage) }
                ).awaitAll()
            }

            if (results.all { it }) {
                sendSideEffect(ProfileSettingSideEffect.NavigateMyPage)
            }
        }
    }

    private suspend fun updateNickName(nickName: String): Boolean {
        val result = updateNickNameUseCase.invoke(nickName)
        return if (result.isSuccess) {
            true
        } else {
            _state.update { it.copy(isNickNameError = true) }
            false
        }
    }
    private suspend fun updateProfileImage(imageData: ImageMetadata): Boolean {
        val result = updateProfileImageUseCase.invoke(imageData)
        return if (result.isSuccess) {
            true
        }
        else {
            _state.update { it.copy(isImageError = true) }
            false
        }
    }

    private fun observeStateChanges() {
        viewModelScope.launch {
            _state.collectLatest { currentState ->
                val isFormValid = checkFormValid(currentState)
                if (currentState.isCompletedEnabled != isFormValid) {
                    _state.update { it.copy(isCompletedEnabled = isFormValid) }
                }
            }
        }
    }

    private fun checkFormValid(state: ProfileSettingState): Boolean {
        return state.changedNickName.isNotBlank() &&
                state.isNickNameError.not() &&
                state.isImageError.not() &&
                (state.changedNickName != state.profile.nickName || state.uploadImage.uri != state.profile.profileImage)

    }

    private fun sendSideEffect(effect: ProfileSettingSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class ProfileSettingState(
        var changedNickName: String = MemberUiModel.getDefault().nickName,
        var uploadImage: ImageMetadata = ImageMetadata.getDefault(),
        var profile: MemberUiModel = MemberUiModel.getDefault(),
        var isNickNameError: Boolean = false,
        var isImageError: Boolean = false,
        var isCompletedEnabled: Boolean = false
    )

    sealed class ProfileSettingSideEffect {
        data object ShowPhotoPicker: ProfileSettingSideEffect()
        data object NavigateMyPage: ProfileSettingSideEffect()
        data object NavigateHome: ProfileSettingSideEffect()
    }
}