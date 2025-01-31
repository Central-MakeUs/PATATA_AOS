package com.cmc.presentation.map.selectlocation

import androidx.fragment.app.viewModels
import com.cmc.common.base.BaseFragment
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentSelectLocationBinding
import com.cmc.presentation.map.MarkerManager
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import dagger.hilt.android.AndroidEntryPoint
import com.cmc.presentation.map.selectlocation.SelectLocationViewModel.SelectLocationState
import com.cmc.presentation.map.selectlocation.SelectLocationViewModel.SelectLocationSideEffect

@AndroidEntryPoint
class SelectLocationFragment: BaseFragment<FragmentSelectLocationBinding>(R.layout.fragment_select_location),
    OnMapReadyCallback {

    private val viewModel: SelectLocationViewModel by viewModels()

    private lateinit var naverMap: NaverMap
    private lateinit var markerManager: MarkerManager

    override fun initObserving() {

    }
    override fun initView() {
        setMap()
        setAppBar()
    }

    private fun updateUI(state: SelectLocationState) {

    }
    private fun handleEffect(effect: SelectLocationSideEffect) {

    }

    private fun setMap() { binding.viewSelectLocationMap.getMapAsync(this) }
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

        naverMap.uiSettings.apply {
            isZoomControlEnabled = false
            isScaleBarEnabled = false
            isLogoClickEnabled = false
        }
    }
}