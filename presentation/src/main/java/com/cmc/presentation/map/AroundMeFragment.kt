package com.cmc.presentation.map

import com.cmc.common.base.BaseFragment
import com.cmc.common.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentAroundMeBinding
import com.naver.maps.map.MapView

class AroundMeFragment: BaseFragment<FragmentAroundMeBinding>(R.layout.fragment_around_me) {

    private lateinit var mapView: MapView

    override fun initObserving() {

    }

    override fun initView() {
        mapView = binding.viewMap

        setCategory()
    }

    private fun setCategory() {
        val categoryTabs: List<Pair<Int, Int?>> = SpotCategory.entries
            .map { it.stringId to it.resId}

        binding.tabMapFilter.setTabList(categoryTabs)

        mapView.getMapAsync { naverMap ->

            naverMap.uiSettings.apply {
                isZoomControlEnabled = false
                isScaleBarEnabled = false
                isLogoClickEnabled = false
            }

        }

    }
}