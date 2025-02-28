package com.cmc.presentation.home.view

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.forEachIndexed
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentHomeBinding
import com.cmc.presentation.home.adapter.SpotHorizontalCardAdapter
import com.cmc.presentation.home.adapter.SpotPolaroidAdapter
import com.cmc.presentation.home.viewmodel.HomeViewModel
import com.cmc.presentation.home.viewmodel.HomeViewModel.HomeSideEffect
import com.cmc.presentation.home.viewmodel.HomeViewModel.HomeState
import com.cmc.presentation.map.model.TodayRecommendedSpotUiModel
import com.cmc.presentation.map.model.TodayRecommendedSpotWithHomeUiModel
import com.cmc.presentation.model.SpotCategoryItem
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment: BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var categoryViews: List<Pair<View, SpotCategory>>
    private lateinit var categoryRecommendAdapter: SpotHorizontalCardAdapter
    private lateinit var spotRecommendAdapter: SpotPolaroidAdapter


    override fun initObserving() {
        repeatWhenUiStarted {
            launch { viewModel.state.collectLatest { state -> updateUI(state) } }
            launch { viewModel.sideEffect.collectLatest { effect -> handleSideEffect(effect) } }
        }
    }
    override fun initView() {
        setButton()
        initSpotCategory()
        initHorizontalCardView()
        initCategoryTab()
    }

    private var previousState: HomeState? = null
    private fun updateUI(state: HomeState) {
        state.selectedCategory?.let { configureSelectedSpotCategory(it) }

        binding.layoutCategoryRecommendMore.isVisible = state.categorySpots.size >= 3
        binding.layoutCategoryTabNoResult.isVisible = state.categorySpots.isEmpty()

        categoryRecommendAdapter.setItems(state.categorySpots)

        if (previousState?.recommendedSpots != state.recommendedSpots){
            if (state.recommendedSpots.isNotEmpty()) {
                initTodayRecommendedSpotView(state.recommendedSpots, true)
                spotRecommendAdapter.setLoadingState(false)
            } else {
                initTodayRecommendedSpotView(state.recommendedSpots, true)
            }
        }
        previousState = state
    }
    private fun handleSideEffect(effect: HomeSideEffect) {
        when (effect) {
            is HomeSideEffect.NavigateTodayRecommendedSpot -> { navigateTodaySpotRecommended() }
            is HomeSideEffect.NavigateSpotDetail -> { navigateSpotDetail(effect.spotId) }
            is HomeSideEffect.NavigateSearch -> { navigateSearch() }
            is HomeSideEffect.NavigateCategorySpot -> {  navigateCategorySpot(effect.categoryId) }
        }
    }


    private fun initTodayRecommendedSpotView(items: List<TodayRecommendedSpotWithHomeUiModel>, isLoading: Boolean = true) {
        spotRecommendAdapter = SpotPolaroidAdapter(
            initialItems = items,
            onArchiveClick = { spotId -> viewModel.onClickSpotScrapButton(spotId) },
            onImageClick = { spotId -> viewModel.onClickSpotImage(spotId) },
            isLoading = isLoading,
        )

        binding.vpSpotRecommend.setAdapter(spotRecommendAdapter)
    }
    private fun setButton() {
        binding.searchbarHome.setOnSearchBarClickListener { viewModel.onClickSearchBar() }

        binding.tvSpotPolaroidShowAll.setOnClickListener { viewModel.onClickTodayRecommendedSpotMoreButton() }
        binding.layoutCategoryRecommendMore.setOnClickListener {
            viewModel.onClickCategoryRecommendMoreButton()
        }
    }
    private fun initSpotCategory() {
        categoryViews = listOf(
            binding.viewSpotCategory.layoutCategoryRecommend to SpotCategory.RECOMMEND,
            binding.viewSpotCategory.layoutCategorySnap to SpotCategory.SNAP,
            binding.viewSpotCategory.layoutCategoryNightView to SpotCategory.NIGHT,
            binding.viewSpotCategory.layoutCategoryEverydayLife to SpotCategory.EVERYDAY,
            binding.viewSpotCategory.layoutCategoryNature to SpotCategory.NATURE
        )

        categoryViews.forEach { (layout, category) ->
            layout.setOnClickListener {
                viewModel.onClickSpotCategoryButton(category)
            }
        }
    }
    private fun initHorizontalCardView() {
        categoryRecommendAdapter = SpotHorizontalCardAdapter(
            onArchiveClick = { spotId -> viewModel.onClickSpotScrapButton(spotId = spotId) },
            onImageClick = { spotId -> viewModel.onClickSpotImage(spotId) }
        )

        binding.rvSpotCategory.apply {
            suppressLayout(false)
            layoutManager = LinearLayoutManager(context)
            this.adapter = categoryRecommendAdapter
        }

    }
    private fun initCategoryTab() {
        // 탭 생성
        val categories = SpotCategory.entries.map { SpotCategoryItem(it) }
        categories.forEach { category ->
            binding.tabCategoryFilter.addTab(
                binding.tabCategoryFilter
                    .newTab()
                    .setText(category.getName())
                    .setTag(category.categoryItem)
            )
        }

        // 탭 간격 조절
        val tabs = binding.tabCategoryFilter.getChildAt(0) as ViewGroup
        tabs.forEachIndexed { index, view ->
            val layoutParams = view.layoutParams as LinearLayout.LayoutParams
            layoutParams.setMargins(10, 0, 10, 0)
        }
        binding.tabCategoryFilter.requestLayout()

        // 탭 선택 이벤트
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

    private fun configureSelectedSpotCategory(category: SpotCategory) {
        categoryViews.forEach {
            it.first.isSelected = it.second == category
        }
    }

    override fun onResume() {
        super.onResume()
        if (::spotRecommendAdapter.isInitialized) {
            binding.vpSpotRecommend.setAdapter(spotRecommendAdapter)
        }
        viewModel.refreshHomeScreen()
    }

    private fun navigateTodaySpotRecommended() { navigate(R.id.navigate_today_spot_recommendation) }
    private fun navigateSpotDetail(spotId: Int) { (activity as GlobalNavigation).navigateSpotDetail(spotId) }
    private fun navigateSearch() { (activity as GlobalNavigation).navigateSearch() }
    private fun navigateCategorySpot(categoryId: Int) {
        (activity as GlobalNavigation).navigateCategorySpots(categoryId)
    }
}