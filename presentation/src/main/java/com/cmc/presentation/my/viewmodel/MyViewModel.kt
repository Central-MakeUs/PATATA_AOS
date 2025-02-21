package com.cmc.presentation.my.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.feature.auth.usecase.ClearTokenUseCase
import com.cmc.domain.feature.auth.usecase.GetMyProfileUseCase
import com.cmc.domain.feature.spot.usecase.GetMySpotsUseCase
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.login.model.MemberUiModel
import com.cmc.presentation.login.model.toUiModel
import com.cmc.presentation.spot.model.SpotPreviewUiModel
import com.cmc.presentation.spot.model.toListUiModel
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
class MyViewModel @Inject constructor(
    private val getMySpotsUseCase: GetMySpotsUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MyState())
    val state: StateFlow<MyState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<MySideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()


    fun refreshMyPageScreen() {
        viewModelScope.launch {
            fetchMyProfile()
            fetchMySpots()
        }
    }

    private suspend fun fetchMyProfile() {
        getMyProfileUseCase.invoke()
            .onSuccess {  member ->
                _state.update {
                    it.copy(profile = member.toUiModel())
                }
            }.onFailure {  e ->
                e.printStackTrace()
            }
    }
    private suspend fun fetchMySpots() {
        getMySpotsUseCase.invoke()
            .onSuccess { spots ->
                _state.update {
                    it.copy(spots = spots.toListUiModel())
                }
            }
            .onFailure {  }
    }

    fun onClickSettingButton() {
        viewModelScope.launch {
            _sideEffect.emit(MySideEffect.NavigateToSetting)
        }
    }
    fun onClickExploreSpotButton() {
        sendSideEffect(MySideEffect.NavigateToCategorySpots(SpotCategory.ALL.id))
    }
    fun onClickSpotImage(spotId: Int) {
        sendSideEffect(MySideEffect.NavigateSpotDetail(spotId))
    }
    fun onClickChangeProfileButton() {
        val profile = state.value.profile
        sendSideEffect(MySideEffect.NavigateSettingProfile(profile.nickName, profile.profileImage))
    }

    private fun sendSideEffect(effect: MySideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class MyState(
        val spots: List<SpotPreviewUiModel> = emptyList(),
        val profile: MemberUiModel = MemberUiModel.getDefault(),
    )

    sealed class MySideEffect {
        data object NavigateToSetting : MySideEffect()
        data class NavigateToCategorySpots(val categoryId: Int) : MySideEffect()
        data class NavigateSpotDetail(val spotId: Int) : MySideEffect()
        data class NavigateSettingProfile(val nickName: String, val profileImage: String) : MySideEffect()
        data class ShowSnackBar(val message: String) : MySideEffect()
    }
}