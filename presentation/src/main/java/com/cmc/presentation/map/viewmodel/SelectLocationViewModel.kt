package com.cmc.presentation.map.viewmodel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmc.domain.base.exception.ApiException
import com.cmc.domain.feature.location.Location
import com.cmc.domain.feature.spot.usecase.CheckSpotRegistration
import com.cmc.presentation.map.model.CheckSpotRegistrationResponse
import com.cmc.presentation.util.toLocation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    private val checkSpotRegistration: CheckSpotRegistration,
): ViewModel() {

    private val _state = MutableStateFlow(SelectLocationState())
    val state: StateFlow<SelectLocationState> = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<SelectLocationSideEffect>()
    val sideEffect: SharedFlow<SelectLocationSideEffect> = _sideEffect.asSharedFlow()

    fun onClickSelectionCompleteButton() {
        val addressName = _state.value.currentTargetAddress
        val latLng = _state.value.currentTargetLatLng

        viewModelScope.launch {
            checkSpotRegistration.invoke(latitude = latLng.latitude, longitude = latLng.longitude)
                .onSuccess {
                    _sideEffect.emit(SelectLocationSideEffect.NavigateToAddSpot(addressName, latLng))
                }.onFailure { e ->
                    when (e) {
                        is ApiException.RegistrationLimitExceeded -> {
                            val gson = Gson()
                            if (e.data is List<*>) {
                                val json = gson.toJson(e.data)
                                val type = object : TypeToken<List<CheckSpotRegistrationResponse>>() {}.type
                                val result: List<CheckSpotRegistrationResponse> = Gson().fromJson(json, type)
                                _state.update {
                                    it.copy(
                                        nearBySpots = result
                                    )
                                }
                            }
                        }
                    }
                }
        }
    }

    fun initCurrentTargetLocation(location: Location) {
        _state.update { it.copy(currentTargetLatLng = location) }
    }
    fun initCameraPosition() {
        sendSideEffect(SelectLocationSideEffect.UpdateCurrentLocation(state.value.currentTargetLatLng))
    }

    fun changeCurrentTargetLocation(targetLocation : LatLng) {
        _state.update {
            it.copy(
                currentTargetLatLng = targetLocation.toLocation(),
                nearBySpots = emptyList()
            )
        }
        changeCurrentTargetAddress(targetLocation.toLocation())
    }

    private fun changeCurrentTargetAddress(location: Location) {
        val geocoder = Geocoder(context, Locale.KOREAN)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                    updateAddressState(addresses)
                }
            } else {
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
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

    private fun sendSideEffect(effect: SelectLocationSideEffect) {
        viewModelScope.launch {
            _sideEffect.emit(effect)
        }
    }

    data class SelectLocationState(
        val isLoading: Boolean = false,
        val nearBySpots: List<CheckSpotRegistrationResponse> = emptyList(),
        var currentTargetAddress: String = "",
        val currentTargetLatLng: Location = Location(0.0, 0.0)
    )

    sealed class SelectLocationSideEffect {
        data class NavigateToAddSpot(val addressName: String, val location: Location): SelectLocationSideEffect()
        data class UpdateCurrentLocation(val location: Location): SelectLocationSideEffect()
    }
}