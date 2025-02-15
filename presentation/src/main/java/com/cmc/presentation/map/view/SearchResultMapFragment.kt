package com.cmc.presentation.map.view

import com.cmc.common.base.BaseFragment
import com.cmc.common.constants.NavigationKeys
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentSearchResultMapBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchResultMapFragment: BaseFragment<FragmentSearchResultMapBinding>(R.layout.fragment_search_result_map) {
    override fun initObserving() {
    }

    override fun initView() {
        arguments?.let {
            val latitude = it.getDouble(NavigationKeys.AddSpot.ARGUMENT_LATITUDE)
            val longitude = it.getDouble(NavigationKeys.AddSpot.ARGUMENT_LONGITUDE)
            val keyword = it.getString(NavigationKeys.Search.ARGUMENT_KEYWORD)
            // TODO: ViewModel의 State로 좌표, keyword 다 넘기기
            // TODO: updateUI의 keyword를 서치바에 반영하기
            keyword?.let { binding.appbar.setSearchText(it) }
        }
    }
}