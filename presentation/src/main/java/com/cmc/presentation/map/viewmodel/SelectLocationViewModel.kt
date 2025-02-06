package com.cmc.presentation.map.viewmodel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SelectLocationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
): ViewModel() {

    private val _state = MutableStateFlow(SelectLocationState())
    val state: StateFlow<SelectLocationState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SelectLocationSideEffect>()
    val sideEffect: SharedFlow<SelectLocationSideEffect> = _sideEffect.asSharedFlow()

    fun onClickSelectionCompleteButton() {
        val addressName = _state.value.currentTargetAddress
        val latLng = _state.value.currentTargetLatLng

        viewModelScope.launch {
            _sideEffect.emit(SelectLocationSideEffect.NavigateToAddSpot(addressName, latLng))
        }
    }

    fun changeCurrentTargetLocation(latLng : LatLng) {
        _state.update {
            it.copy(currentTargetLatLng = latLng)
        }
        changeCurrentTargetAddress(latLng)
    }

    private fun changeCurrentTargetAddress(latLng: LatLng) {
        val geocoder = Geocoder(context, Locale.KOREAN)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) { addresses ->
                    updateAddressState(addresses)
                }
            } else {
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                updateAddressState(addresses)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            _state.update {
                it.copy(currentTargetAddress = "주소를 가져올 수 없습니다.")
            }
        }
    }
    private fun updateAddressState(addresses: List<Address>?) {
        val addressName = addresses?.firstOrNull()?.getAddressLine(0)
        _state.update {
            it.copy(currentTargetAddress = addressName ?: "주소를 찾을 수 없습니다.")
        }
    }

    data class SelectLocationState(
        val isLoading: Boolean = false,
        var currentTargetAddress: String = "",
        val currentTargetLatLng: LatLng = LatLng(0.0, 0.0)
    )

    sealed class SelectLocationSideEffect {
        data class NavigateToAddSpot(val addressName: String, val latLng: LatLng): SelectLocationSideEffect()
    }
}