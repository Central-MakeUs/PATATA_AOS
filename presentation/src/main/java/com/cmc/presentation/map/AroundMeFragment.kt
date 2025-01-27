package com.cmc.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.cmc.common.base.BaseFragment
import com.cmc.common.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentAroundMeBinding
import com.naver.maps.map.MapView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.cmc.presentation.map.AroundMeViewModel.AroundMeSideEffect

@AndroidEntryPoint
class AroundMeFragment: BaseFragment<FragmentAroundMeBinding>(R.layout.fragment_around_me) {

    private val viewModel: AroundMeViewModel by viewModels()
    private lateinit var mapView: MapView

    override fun initObserving() {
        repeatWhenUiStarted {
            launch {
                viewModel.state.collectLatest {

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
            naverMap.uiSettings.apply {
                isZoomControlEnabled = false
                isScaleBarEnabled = false
                isLogoClickEnabled = false
            }

            viewModel.checkLocationPermission()
        }
    }

    private fun setCategory() {
        val categoryTabs: List<Pair<Int, Int?>> = SpotCategory.entries
            .map { it.stringId to it.resId}

        binding.tabMapFilter.setTabList(categoryTabs)
    }
    
    private fun setButton() {
        binding.layoutAddLocation.setOnClickListener {
            viewModel.checkLocationPermission()
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