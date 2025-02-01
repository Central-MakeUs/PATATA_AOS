package com.cmc.presentation.map.selectlocation

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.cmc.common.base.BaseFragment
import com.cmc.common.constants.NavigationKeys
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentSelectLocationBinding
import com.cmc.presentation.map.MarkerManager
import com.cmc.presentation.map.selectlocation.SelectLocationViewModel.SelectLocationSideEffect
import com.cmc.presentation.map.selectlocation.SelectLocationViewModel.SelectLocationState
import com.naver.maps.geometry.LatLng
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
            launch {
                viewModel.state.collectLatest { state ->
                    updateUI(state)
                }
            }
            launch {
                viewModel.sideEffect.collect { effect ->
                    handleEffect(effect)
                }
            }
        }
    }
    override fun initView() {
        setMap()
        setAppBar()
        seCompleteButtonListener()
    }

    private fun seCompleteButtonListener() {
        binding.layoutSelectionCompleteButton.setOnClickListener {
            viewModel.onClickSelectionCompleteButton()
        }
    }

    private fun updateUI(state: SelectLocationState) {
        binding.etSelectionToShare.setHint(state.currentTargetAddress)
    }
    private fun handleEffect(effect: SelectLocationSideEffect) {
        when (effect) {
            is SelectLocationSideEffect.NavigateToAddSpot -> {
                navigate(R.id.navigate_add_spot, Bundle().apply {
                    putString(NavigationKeys.AddSpot.ARGUMENT_ADDRESS_NAME, effect.addressName)
                    putDouble(NavigationKeys.AddSpot.ARGUMENT_LATITUDE, effect.latLng.latitude)
                    putDouble(NavigationKeys.AddSpot.ARGUMENT_LONGITUDE, effect.latLng.longitude)
                })
            }
        }
    }

    private fun setMap() {
        binding.viewSelectLocationMap.getMapAsync(this)
    }
    private fun setAppBar() {
        binding.selectLocationAppbar.apply {
            setupAppBar(
                getString(R.string.title_add_a_spot),
                onHeadButtonClick = { finish() }
            )
        }
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        markerManager = MarkerManager(naverMap)

        with(naverMap) {
            uiSettings.apply {
                isZoomControlEnabled = false
                isScaleBarEnabled = false
                isLogoClickEnabled = false
            }

            setTargetPointViewPosition(cameraPosition.target, projection)

            addOnCameraIdleListener {
                viewModel.changeCurrentTargetLocation(
                    naverMap.cameraPosition.target
                )
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

}