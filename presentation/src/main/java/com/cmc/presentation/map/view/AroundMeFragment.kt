package com.cmc.presentation.map.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_CANCELED
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.cmc.common.base.BaseFragment
import com.cmc.common.constants.NavigationKeys
import com.cmc.design.component.PatataAlert
import com.cmc.domain.feature.location.Location
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentAroundMeBinding
import com.cmc.presentation.map.manager.MarkerManager
import com.cmc.presentation.map.viewmodel.AroundMeViewModel
import com.cmc.presentation.map.viewmodel.AroundMeViewModel.AroundMeSideEffect
import com.cmc.presentation.model.SpotCategoryItem
import com.cmc.presentation.map.model.MapScreenLocation
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AroundMeFragment: BaseFragment<FragmentAroundMeBinding>(R.layout.fragment_around_me), OnMapReadyCallback {

    private val viewModel: AroundMeViewModel by viewModels()

    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private lateinit var markerManager: MarkerManager

    override fun initObserving() {
        repeatWhenUiStarted {
            launch {
                viewModel.state.collect { state -> updateUI(state) }
            }

            launch {
                viewModel.sideEffect.collectLatest { effect -> handleEffect(effect) }
            }
        }
    }
    override fun initView() {
        setAppBar()
        setMap()
        setCategory()
        setButton()
    }

    private fun updateUI(state: AroundMeViewModel.AroundMeState) {
        state.results.let {
            if (::markerManager.isInitialized) {
                markerManager.updateMarkersWithData(it)
            }
        }
        if (state.selectedTabPosition != null && state.selectedTabPosition != binding.tabMapFilter.getSelectedTabPosition()) {
            binding.tabMapFilter.setSelectedTabPosition(state.selectedTabPosition)
        }

        binding.layoutExploreThisArea.isVisible = state.exploreVisible
    }
    private fun handleEffect(effect: AroundMeSideEffect) {
        when (effect) {
            is AroundMeSideEffect.RequestLocationPermission -> { checkLocationRequest() }
            is AroundMeSideEffect.NavigateAddLocation -> { navigateAddLocation(effect.location) }
            is AroundMeSideEffect.NavigateSearch -> { navigateSearch(effect.location) }
            is AroundMeSideEffect.UpdateCurrentLocation -> { moveCameraPosition(effect.location) }
        }
    }

    private fun setAppBar() {
        binding.appbar.setupAppBar(
            onBodyClick = { viewModel.onClickSearchBar(naverMap.cameraPosition.target) },
            searchBarDisable = true
        )
    }
    private fun setMap() {
        mapView = binding.viewMap
        mapView.getMapAsync(this)
    }
    private fun setCategory() {
        val categoryTabs: List<Pair<String, Drawable?>> = SpotCategory.entries
            .map {
                val item = SpotCategoryItem(it)
                getString(item.getName()) to item.getIcon()?.let { resId ->
                    ContextCompat.getDrawable(requireContext(), resId)
                }
            }

        binding.tabMapFilter.apply {
            setTabSelectedListener { position ->
                viewModel.onClickCategoryTab(position)
            }
            setTabList(categoryTabs)
        }
    }
    private fun setButton() {
        with(binding) {
            ivAddLocation.setOnClickListener { viewModel.onClickAddLocationButton(naverMap.cameraPosition.target) }
            ivCurrentLocation.setOnClickListener { viewModel.getCurrentLocation() }
            layoutExploreThisArea.setOnClickListener { viewModel.onClickExploreThisArea() }
        }
    }

    override fun onMapReady(map: NaverMap) {
        viewModel.getCurrentLocation()
        naverMap = map

        with(naverMap) {
            markerManager = MarkerManager(this@with)

            uiSettings.apply {
                isZoomControlEnabled = false
                isScaleBarEnabled = false
                isLogoClickEnabled = false
            }

            addOnCameraChangeListener { reason, _ ->
                if (reason == CameraUpdate.REASON_GESTURE) { viewModel.movedCameraPosition() }
            }
            addOnCameraIdleListener {
                viewModel.updateMapScreenLocation(getMapScreenLocation())
            }
        }
    }

    private fun getMapScreenLocation(): MapScreenLocation {
        val bounds = naverMap.contentBounds
        val southWest = bounds.southWest
        val northEast = bounds.northEast

        return MapScreenLocation(
            southWest.latitude,
            southWest.longitude,
            northEast.latitude,
            northEast.longitude
        )
    }

    private fun moveCameraPosition(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        val cameraUpdate = CameraUpdate.scrollTo(latLng)
        naverMap.moveCamera(cameraUpdate)
    }

    private fun showLocationPermissionDialog() {
        PatataAlert(requireContext())
            .title("위치 권한 요청")
            .content("위치 권한 설정 창으로 이동할까요?")
            .multiButton {
                leftButton("네") {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", activity?.packageName, null)
                    )
                    openAppSettingsLauncher.launch(intent)
                }
                rightButton("아니오") {
                    Toast.makeText(requireContext(), "위치 권한이 꼭 필요합니다.", Toast.LENGTH_SHORT).show()
                }
            }.show()
    }

    private fun navigateAddLocation(location: Location) {
        navigate(R.id.navigate_around_me_to_select_location, Bundle().apply {
            putDouble(NavigationKeys.AddSpot.ARGUMENT_LATITUDE, location.latitude)
            putDouble(NavigationKeys.AddSpot.ARGUMENT_LONGITUDE, location.longitude)
        })
    }
    private fun navigateSearch(location: Location) {
        navigate(R.id.navigate_around_me_to_search_input, Bundle().apply {
            putDouble(NavigationKeys.AddSpot.ARGUMENT_LATITUDE, location.latitude)
            putDouble(NavigationKeys.AddSpot.ARGUMENT_LONGITUDE, location.longitude)
        })
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
    private val openAppSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // 설정창에서 뒤로가기로 앱에 돌아오는 경우 CANCELED로 처리됨
        if (result.resultCode == RESULT_OK || result.resultCode == RESULT_CANCELED) {
            checkLocationRequest()
        }
    }
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            Toast.makeText(context, "위치 권한이 허용되었습니다!", Toast.LENGTH_SHORT).show()
        } else {
            showLocationPermissionDialog()
        }
    }
}