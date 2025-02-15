package com.cmc.presentation.map.view

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.cmc.common.base.BaseFragment
import com.cmc.common.constants.NavigationKeys
import com.cmc.domain.feature.location.Location
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentSearchInputBinding
import com.cmc.presentation.map.viewmodel.SearchInputViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.cmc.presentation.map.viewmodel.SearchInputViewModel.SearchInputSideEffect
import com.cmc.presentation.map.viewmodel.SearchInputViewModel.SearchInputState

@AndroidEntryPoint
class SearchInputFragment: BaseFragment<FragmentSearchInputBinding>(R.layout.fragment_search_input) {

    private val viewModel: SearchInputViewModel by viewModels()

    override fun initObserving() {
        repeatWhenUiStarted {
            launch {
                viewModel.state.collect { state -> updateUI(state) }
            }
            launch {
                viewModel.sideEffect.collectLatest { effect ->
                    when (effect) {
                        is SearchInputSideEffect.NavigateSearchResultMap -> {
                            navigateSearchResultMap(effect.keyword, effect.location)
                        }
                        is SearchInputSideEffect.Finish -> { finish() }
                    }
                }
            }
        }
    }
    override fun initView() {
        arguments?.let {
            val latitude = it.getDouble(NavigationKeys.AddSpot.ARGUMENT_LATITUDE)
            val longitude = it.getDouble(NavigationKeys.AddSpot.ARGUMENT_LONGITUDE)
            viewModel.setTargetLocation(latitude, longitude)
        }
        setAppBar()
    }

    private fun updateUI(state: SearchInputState) {
    }

    private fun setAppBar() {
        binding.appbar.apply {
            focusSearchInput()
            setupAppBar(
                onHeadButtonClick = { viewModel.onClickHeaderButton() },
                onSearch = { str -> viewModel.submitSearchBar(str) }
            )
        }
    }

    private fun navigateSearchResultMap(keyword: String, location: Location) {
        navigate(R.id.navigate_search_input_to_search_result_map, Bundle().apply{
            putDouble(NavigationKeys.AddSpot.ARGUMENT_LATITUDE, location.latitude)
            putDouble(NavigationKeys.AddSpot.ARGUMENT_LATITUDE, location.longitude)
            putString(NavigationKeys.Search.ARGUMENT_KEYWORD, keyword)
        })
    }
}