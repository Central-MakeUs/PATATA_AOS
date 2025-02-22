package com.cmc.presentation.selectlocation.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.common.constants.NavigationKeys
import com.cmc.domain.feature.location.Location
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentSelectLocationBinding
import com.cmc.presentation.map.manager.MarkerManager
import com.cmc.presentation.map.model.SpotWithMapUiModel
import com.cmc.presentation.selectlocation.viewmodel.SelectLocationViewModel
import com.cmc.presentation.selectlocation.viewmodel.SelectLocationViewModel.SelectLocationSideEffect
import com.cmc.presentation.selectlocation.viewmodel.SelectLocationViewModel.SelectLocationState
import com.cmc.presentation.util.toLatLng
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.Projection
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectLocationFragment: BaseFragment<FragmentSelectLocationBinding>(R.layout.fragment_select_location),
    OnMapReadyCallback {

    private val viewModel: SelectLocationViewModel by viewModels()

    private lateinit var naverMap: NaverMap
    private lateinit var markerManager: MarkerManager

    override fun initObserving() {
        repeatWhenUiStarted {
            launch { viewModel.state.collectLatest { state -> updateUI(state) } }
            launch { viewModel.sideEffect.collect { effect -> handleEffect(effect) } }
        }
    }
    override fun initView() {
        arguments?.let {
            val isEdit = it.getBoolean(NavigationKeys.Location.ARGUMENT_IS_EDIT)
            val latitude = it.getDouble(NavigationKeys.Location.ARGUMENT_LATITUDE)
            val longitude = it.getDouble(NavigationKeys.Location.ARGUMENT_LONGITUDE)

            viewModel.initIsEditState(isEdit)
            viewModel.initCurrentTargetLocation(Location(latitude, longitude))

            setAppBar(isEdit)
        }
        setMap()
        seCompleteButtonListener()
    }

    private fun seCompleteButtonListener() {
        binding.layoutSelectionCompleteButton.setOnClickListener {
            viewModel.onClickSelectionCompleteButton()
        }
    }
    private var previousState: SelectLocationState? = null
    private fun updateUI(state: SelectLocationState) {
        binding.etSelectionToShare.setHint(state.currentTargetAddress)
        if (previousState?.nearBySpots != state.nearBySpots && ::markerManager.isInitialized) {
            val nearBySpotWithMapList = state.nearBySpots.map { SpotWithMapUiModel.getDefault().copy(
                latitude = it.latitude,
                longitude = it.longitude,
                categoryId = SpotCategory.getLastItem().id + 1
            ) }
            markerManager.updateMarkersWithData(nearBySpotWithMapList)
            if (state.nearBySpots.isNotEmpty()) {
                markerManager.showRegistrationLimitArea(state.currentTargetLatLng.toLatLng())
            } else {
                markerManager.clearRegistrationLimitArea()
            }
        }
        previousState = state
    }
    private fun handleEffect(effect: SelectLocationSideEffect) {
        when (effect) {
            is SelectLocationSideEffect.NavigateAddSpot -> {
                navigateAddSpot(effect.addressName, effect.location.latitude, effect.location.longitude)
            }
            is SelectLocationSideEffect.NavigateEditSpot -> {
                navigateEditSpot(effect.addressName, effect.location.latitude, effect.location.longitude)
            }
            is SelectLocationSideEffect.UpdateCurrentLocation -> { moveCameraPosition(effect.location) }
        }
    }

    private fun setMap() {
        binding.viewSelectLocationMap.getMapAsync(this)
    }
    private fun setAppBar(isEdit: Boolean) {
        binding.selectLocationAppbar.apply {
            setupAppBar(
                getString(if (isEdit)  R.string.title_edit_a_spot else R.string.title_add_a_spot),
                onHeadButtonClick = { finish() }
            )
        }
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        markerManager = MarkerManager(requireContext(), naverMap)

        viewModel.initCameraPosition()

        with(naverMap) {
            uiSettings.apply {
                isZoomControlEnabled = false
                isScaleBarEnabled = false
                isLogoClickEnabled = false
            }

            setTargetPointViewPosition(cameraPosition.target, projection)

            addOnCameraIdleListener {
                viewModel.changeCurrentTargetLocation(naverMap.cameraPosition.target)
            }
        }
    }

    private fun setTargetPointViewPosition(centerLatLng: LatLng, projection: Projection) {
        val screenPosition = projection.toScreenLocation(centerLatLng)

        binding.ivTargetLocationPoint.apply {
            x = screenPosition.x - (width / 2)
            y = screenPosition.y - height + binding.selectLocationAppbar.height
        }
    }
    private fun moveCameraPosition(location: Location) {
        val cameraUpdate = CameraUpdate.scrollTo(location.toLatLng())
        naverMap.moveCamera(cameraUpdate)
    }

    private fun navigateAddSpot(addressName: String, latitude: Double, longitude: Double) {
        (activity as GlobalNavigation).navigateAddSpot(addressName, latitude, longitude)
    }
    private fun navigateEditSpot(addressName: String, latitude: Double, longitude: Double) {
        val resultBundle = Bundle().apply {
            putString("addressName", addressName)
            putDouble("latitude", latitude)
            putDouble("longitude", longitude)
        }
        findNavController().previousBackStackEntry?.savedStateHandle?.set("resultKey", resultBundle)
        findNavController().popBackStack()

    }
//    private fun navigateEditSpot(spotId: Int, latitude: Double, longitude: Double) {
//        (activity as GlobalNavigation).navigateEditSpot(spotId, latitude, longitude)
//    }
}