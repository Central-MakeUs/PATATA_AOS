package com.cmc.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.cmc.common.base.BaseFragment
import com.cmc.domain.location.Location
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentAroundMeBinding
import com.naver.maps.map.MapView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.cmc.presentation.map.AroundMeViewModel.AroundMeSideEffect
import com.cmc.presentation.model.SpotCategoryItem
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback

@AndroidEntryPoint
class AroundMeFragment: BaseFragment<FragmentAroundMeBinding>(R.layout.fragment_around_me), OnMapReadyCallback {

    private val viewModel: AroundMeViewModel by viewModels()

    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private lateinit var markerManager: MarkerManager

    override fun initObserving() {
        repeatWhenUiStarted {
            launch {
                viewModel.state.collectLatest { state ->
                    when {
                        state.isLoading -> {}
                        state.errorMessage != null -> {}
                        state.results.isNullOrEmpty().not() -> { markerManager.updateMarkersWithData(state.results) }
                        state.currentLocation != null -> { moveCameraPosition(state.currentLocation) }
                    }
                }
            }

            launch {
                viewModel.sideEffect.collectLatest { effect ->
                    when (effect) {
                        is AroundMeSideEffect.RequestLocationPermission -> {
                            checkLocationRequest()
                        }
                    }
                }
            }
        }
    }

    override fun initView() {
        showLocationPermissionCheck()
        setMap()
        setCategory()
        setButton()
    }

    private fun showLocationPermissionCheck() {
        viewModel.checkLocationPermission()
    }

    private fun setMap() {
        mapView = binding.viewMap
        mapView.getMapAsync(this)
    }

    private fun moveCameraPosition(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        val cameraUpdate = CameraUpdate.scrollTo(latLng)
        naverMap.moveCamera(cameraUpdate)
    }

    private fun setCategory() {
        val categoryTabs: List<Pair<String, Drawable?>> = SpotCategory.entries
            .map {
                val item = SpotCategoryItem(it)
                getString(item.getName()) to item.getIcon()?.let { resId -> ContextCompat.getDrawable(requireContext(), resId) }
            }

        binding.tabMapFilter.setTabList(categoryTabs)
    }
    
    private fun setButton() {
        binding.layoutAddLocation.setOnClickListener {
            viewModel.getDumpData()
            // TODO: 장소 추가하기 화면으로 이동
        }
        
        binding.ivCurrentLocation.setOnClickListener {
            viewModel.getCurrentLocation()
        }
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        if (permissions.all { it.value }) {
            Toast.makeText(context, "위치 권한이 허용되었습니다!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLocationRequest() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (permissions.all {
                ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
            }) {
            locationPermissionRequest.launch(permissions)
        }
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        markerManager = MarkerManager(naverMap)

        naverMap.uiSettings.apply {
            isZoomControlEnabled = false
            isScaleBarEnabled = false
            isLogoClickEnabled = false
        }
    }
}