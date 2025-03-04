package com.cmc.presentation.map.view

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.common.constants.NavigationKeys
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentAroundMeListBinding
import com.cmc.presentation.map.adapter.MapSpotHorizontalMultiImageCardAdapter
import com.cmc.presentation.map.model.MapScreenLocation
import com.cmc.presentation.map.viewmodel.AroundMeListViewModel
import com.cmc.presentation.map.viewmodel.AroundMeListViewModel.AroundMeListSideEffect
import com.cmc.presentation.map.viewmodel.AroundMeListViewModel.AroundMeListState
import com.cmc.presentation.model.SpotCategoryItem
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AroundMeListFragment: BaseFragment<FragmentAroundMeListBinding>(R.layout.fragment_around_me_list) {

    private val viewModel: AroundMeListViewModel by viewModels()

    private lateinit var mapSpotAdapter: MapSpotHorizontalMultiImageCardAdapter

    override fun initObserving() {
        repeatWhenUiStarted {
            launch { viewModel.state.collect { state -> updateUI(state) } }
            launch { viewModel.sideEffect.collectLatest { effect -> handleSideEffect(effect) } }
        }
    }
    override fun initView() {
        initArgument(arguments)
        startShimmer()
        initCategoryTab()
        initAppBar()
        initRecyclerView()
    }

    private fun updateUI(state: AroundMeListState) {
        if (state.isLoading.not()) {
            binding.layoutShimmer.stopShimmer()
            binding.layoutShimmer.isVisible = false
            binding.rvSpotCategory.isVisible = true
        }
        binding.layoutMapListNoResult.isVisible = state.itemCount == 0 && state.isLoading.not()
        viewLifecycleOwner.lifecycleScope.launch {
            mapSpotAdapter.submitData(state.result)
        }
    }
    private fun handleSideEffect(effect: AroundMeListSideEffect) {
        when (effect) {
            is AroundMeListSideEffect.NavigateMap -> { finish() }
            is AroundMeListSideEffect.NavigateSearchInput -> { navigateSearchInput() }
            is AroundMeListSideEffect.NavigateSpotDetail -> { navigateSpotDetail(effect.spotId) }
        }
    }


    private fun initArgument(bundle: Bundle?) {
        bundle?.let {
            val userLatitude = it.getDouble(NavigationKeys.Location.ARGUMENT_LATITUDE)
            val userLongitude = it.getDouble(NavigationKeys.Location.ARGUMENT_LONGITUDE)
            val minLatitude = it.getDouble(NavigationKeys.Location.ARGUMENT_MIN_LATITUDE)
            val minLongitude = it.getDouble(NavigationKeys.Location.ARGUMENT_MIN_LONGITUDE)
            val maxLatitude = it.getDouble(NavigationKeys.Location.ARGUMENT_MAX_LATITUDE)
            val maxLongitude = it.getDouble(NavigationKeys.Location.ARGUMENT_MAX_LONGITUDE)
            val withSearch = it.getBoolean(NavigationKeys.Map.ARGUMENT_WITH_SEARCH)
            viewModel.initScreenLocation(userLatitude, userLongitude, minLatitude, minLongitude, maxLatitude, maxLongitude, withSearch)
        }
    }
    private fun initRecyclerView() {
        mapSpotAdapter = MapSpotHorizontalMultiImageCardAdapter(
            onImageClick = { viewModel.onClickSpotImage(it) },
            onArchiveClick = { viewModel.onClickSpotScrapButton(it) }
        )
        binding.rvSpotCategory.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = mapSpotAdapter
        }
    }
    private fun startShimmer() {
        binding.layoutShimmer.startShimmer()
    }
    private fun initAppBar() {
        binding.aroundMeListAppbar.setupAppBar(
            onHeadButtonClick = { viewModel.onClickHeadButton() },
            onBodyClick = { viewModel.onClickBody() },
            searchBarDisable = true
        )
    }
    private fun initCategoryTab() {
        val categories = SpotCategory.entries.map { SpotCategoryItem(it) }
        categories.forEach { category ->
            binding.tabCategoryFilter.addTab(
                binding.tabCategoryFilter
                    .newTab()
                    .setText(category.getName())
                    .setTag(category.categoryItem)
            )
        }
        binding.tabCategoryFilter.getTabAt(SpotCategory.ALL.id)?.select()
        binding.tabCategoryFilter.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    viewModel.onClickCategoryTab(it.tag as? SpotCategory ?: SpotCategory.ALL)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun navigateSpotDetail(spotId: Int) { (activity as GlobalNavigation).navigateSpotDetail(spotId) }
    private fun navigateSearchInput() { navigate(R.id.navigate_around_me_list_to_search_input) }
}
