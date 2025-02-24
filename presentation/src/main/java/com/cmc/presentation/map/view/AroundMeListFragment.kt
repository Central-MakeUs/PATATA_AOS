package com.cmc.presentation.map.view

import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentAroundMeListBinding
import com.cmc.presentation.home.adapter.SpotHorizontalMultiImageCardAdapter
import com.cmc.presentation.home.viewmodel.HomeViewModel.HomeState
import com.cmc.presentation.map.adapter.MapSpotHorizontalMultiImageCardAdapter
import com.cmc.presentation.map.model.SpotWithMapUiModel
import com.cmc.presentation.map.model.TodayRecommendedSpotUiModel
import com.cmc.presentation.map.viewmodel.AroundMeListViewModel
import com.cmc.presentation.map.viewmodel.AroundMeListViewModel.AroundMeListState
import com.cmc.presentation.map.viewmodel.AroundMeListViewModel.AroundMeListSideEffect
import com.cmc.presentation.map.viewmodel.SharedViewModel
import com.cmc.presentation.model.SpotCategoryItem
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AroundMeListFragment: BaseFragment<FragmentAroundMeListBinding>(R.layout.fragment_around_me_list) {

    private val viewModel: AroundMeListViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var mapSpotAdapter: MapSpotHorizontalMultiImageCardAdapter

    override fun initObserving() {
        repeatWhenUiStarted {
            launch { sharedViewModel.mapSharedData.observe(viewLifecycleOwner) { spots ->
                viewModel.initSpots(spots)
            }}
            launch { viewModel.state.collect { state -> updateUI(state) } }
            launch { viewModel.sideEffect.collectLatest { effect -> handleSideEffect(effect) } }
        }
    }
    override fun initView() {
        initAppBar()
        initCategoryTab()
        initRecyclerView()
    }

    private fun updateUI(state: AroundMeListState) {
        binding.layoutMapListNoResult.isVisible = state.spots.isEmpty() && state.isLoading.not()
        mapSpotAdapter.setItems(state.spots)
    }
    private fun handleSideEffect(effect: AroundMeListSideEffect) {
        when (effect) {
            is AroundMeListSideEffect.NavigateMap -> { finish() }
            is AroundMeListSideEffect.NavigateSearchInput -> { navigateSearchInput() }
            is AroundMeListSideEffect.NavigateSpotDetail -> { navigateSpotDetail(effect.spotId) }
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
