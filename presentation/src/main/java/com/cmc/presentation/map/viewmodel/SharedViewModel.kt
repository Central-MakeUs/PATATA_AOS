package com.cmc.presentation.map.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cmc.presentation.map.model.SpotWithMapUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {
    private val _mapSharedData = MutableLiveData<List<SpotWithMapUiModel>>()
    val mapSharedData: LiveData<List<SpotWithMapUiModel>> = _mapSharedData

    fun sendData(data: List<SpotWithMapUiModel>) {
        _mapSharedData.value = data
    }
}
