package com.cmc.presentation.my.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.feature.auth.model.Member
import com.cmc.domain.feature.auth.usecase.UpdateNickNameUseCase
import com.cmc.presentation.login.model.MemberUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val updateNickNameUseCase: UpdateNickNameUseCase,
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
        _state.update {
            it.copy(
                changedNickName = profile.nickName,
                uploadImage = profile.profileImage,
                profile = profile
            )
        }
    }
    fun setNickName(nickName: String) {
        _state.update {
            it.copy(
                changedNickName = nickName,
                isError = false,
            )
        }
    }

    fun onClickCompleteButton() {
        viewModelScope.launch {
            val result = updateNickNameUseCase.invoke(state.value.changedNickName)
            if (result.isSuccess) {
                _sideEffect.emit(ProfileSettingSideEffect.NavigateMyPage)
            } else {
                _state.update {
                    it.copy(
                        isError = true
                    )
                }
            }
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
                state.isError.not() &&
                (state.changedNickName != state.profile.nickName || state.uploadImage != state.profile.profileImage)

    }


    data class ProfileSettingState(
        var changedNickName: String = MemberUiModel.getDefault().nickName,
        var uploadImage: String = MemberUiModel.getDefault().profileImage,
        var profile: MemberUiModel = MemberUiModel.getDefault(),
        var isError: Boolean = false,
        var isCompletedEnabled: Boolean = false
    )

    sealed class ProfileSettingSideEffect {
        data object NavigateMyPage: ProfileSettingSideEffect()
        data object NavigateHome: ProfileSettingSideEffect()
    }
}