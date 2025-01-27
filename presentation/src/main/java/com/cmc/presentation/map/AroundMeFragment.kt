package com.cmc.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.cmc.common.base.BaseFragment
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentAroundMeBinding
import com.naver.maps.map.MapView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.cmc.presentation.map.AroundMeViewModel.AroundMeSideEffect
import com.cmc.presentation.model.SpotCategoryItem

@AndroidEntryPoint
class AroundMeFragment: BaseFragment<FragmentAroundMeBinding>(R.layout.fragment_around_me) {

    private val viewModel: AroundMeViewModel by viewModels()

    private lateinit var mapView: MapView
    private lateinit var markerManager: MarkerManager

    override fun initObserving() {
        repeatWhenUiStarted {
            launch {
                viewModel.state.collectLatest { state ->
                    when (state.aroundMeStatus) {
                        AroundMeViewModel.AroundMeStatus.LOADING -> {}
                        AroundMeViewModel.AroundMeStatus.SUCCESS -> {
                            markerManager.updateMarkersWithData(state.results)
                        }
                        AroundMeViewModel.AroundMeStatus.ERROR -> {}
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
        setMap()
        setCategory()
        setButton()
    }

    private fun setMap() {
        mapView = binding.viewMap
        mapView.getMapAsync { naverMap ->
            viewModel.checkLocationPermission()

            naverMap.uiSettings.apply {
                isZoomControlEnabled = false
                isScaleBarEnabled = false
                isLogoClickEnabled = false
            }

            markerManager = MarkerManager(naverMap)

        }
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
            // TODO: 현재 위치로 지도 이동하기 구현
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
}