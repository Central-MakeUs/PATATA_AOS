package com.cmc.presentation.spot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.feature.spot.usecase.GetSpotDetailUseCase
import com.cmc.presentation.spot.model.SpotDetailUiModel
import com.cmc.presentation.spot.model.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SpotDetailViewModel @Inject constructor(
    private val getSpotDetailUseCase: GetSpotDetailUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(SpotDetailState())
    val state: StateFlow<SpotDetailState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SpotDetailSideEffect>()
    val sideEffect: SharedFlow<SpotDetailSideEffect> = _sideEffect.asSharedFlow()


    fun getSpotDetail(spotId: Int) {
        viewModelScope.launch {
            getSpotDetailUseCase.invoke(spotId)
                .onSuccess { result ->
                    _state.update {
                        it.copy(spotDetail = result.toUiModel())
                    }
                }
                .onFailure {  }
        }
    }


    fun clickFooterButton(spotIsMine: Boolean) {
        sendSideEffect(SpotDetailSideEffect.ShowBottomSheet(spotIsMine))
    }

    private fun sendSideEffect(effect: SpotDetailSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class SpotDetailState(
        var spotDetail: SpotDetailUiModel? = null,
        val results: List<TempData> = emptyList(),
        val errorMessage: String? = null,
    )

    sealed class SpotDetailSideEffect {
        data class ShowToast(val message: String) : SpotDetailSideEffect()
        data class ShowBottomSheet(val spotIsMine: Boolean): SpotDetailSideEffect()
    }

    data class TempData(
        val title: String,
        val images: List<String>,
        val location: String,
        val description: String,
        val tags: List<String>,
        val comments: List<CommentUiModel>
    )
}