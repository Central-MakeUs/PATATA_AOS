package com.cmc.presentation.login.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.constants.UserPolicy
import com.cmc.domain.feature.auth.usecase.UpdateNickNameUseCase
import com.cmc.domain.feature.auth.usecase.UpdateProfileImageUseCase
import com.cmc.domain.model.ImageMetadata
import com.cmc.presentation.my.viewmodel.ProfileSettingViewModel.ProfileSettingSideEffect
import com.cmc.presentation.my.viewmodel.ProfileSettingViewModel.ProfileSettingState
import com.cmc.presentation.util.uriToImageMetadata
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Deferred
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
class ProfileInputViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val updateNickNameUseCase: UpdateNickNameUseCase,
    private val updateProfileImageUseCase: UpdateProfileImageUseCase,
): ViewModel() {

    private val _state = MutableStateFlow<ProfileInputState>(ProfileInputState())
    val state: StateFlow<ProfileInputState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<ProfileInputSideEffect>()
    val sideEffect: SharedFlow<ProfileInputSideEffect> = _sideEffect.asSharedFlow()

    init {
        observeStateChanges()
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
    fun onClickCompleteButton() {
        viewModelScope.launch {
            val results = coroutineScope {
                val currentState = state.value
                mutableListOf<Deferred<Boolean>>().apply {
                    if (currentState.changedNickName.isNotEmpty())
                        add(async { updateNickName(currentState.changedNickName) })
                    if (currentState.uploadImage.uri.isNotBlank())
                        add(async { updateProfileImage(currentState.uploadImage) })
                }.awaitAll()
            }

            if (results.all { it }) {
                sendSideEffect(ProfileInputSideEffect.NavigateSignUpSuccess)
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


    fun onClickEditProfileImageButton() {
        sendSideEffect(ProfileInputSideEffect.ShowPhotoPicker)
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

    private fun checkFormValid(state: ProfileInputState): Boolean {
        return state.changedNickName.isNotBlank() &&
                state.changedNickName.length >= UserPolicy.MAX_NICKNAME_LENGTH &&
                state.isNickNameError.not() &&
                state.isImageError.not()
    }

    private fun sendSideEffect(effect: ProfileInputSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class ProfileInputState(
        var changedNickName: String = "",
        var uploadImage: ImageMetadata = ImageMetadata.getDefault(),
        var isNickNameError: Boolean = false,
        var isImageError: Boolean = false,
        var isCompletedEnabled: Boolean = false
    )

    sealed class ProfileInputSideEffect {
        data object ShowPhotoPicker: ProfileInputSideEffect()
        data object NavigateSignUpSuccess: ProfileInputSideEffect()
        data object NavigateHome: ProfileInputSideEffect()
    }
}