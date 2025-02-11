package com.cmc.presentation.home.view

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.forEachIndexed
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.common.constants.NavigationKeys
import com.cmc.design.component.SpotPolaroidView
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentHomeBinding
import com.cmc.presentation.home.adapter.SpotHorizontalCardAdapter
import com.cmc.presentation.home.adapter.SpotPolaroidAdapter
import com.cmc.presentation.home.viewmodel.HomeViewModel
import com.cmc.presentation.home.viewmodel.HomeViewModel.HomeSideEffect
import com.cmc.presentation.home.viewmodel.HomeViewModel.HomeState
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


    override fun initObserving() {
        repeatWhenUiStarted {
            launch { viewModel.state.collect { state -> updateUI(state) } }
            launch { viewModel.sideEffect.collectLatest { effect -> handleSideEffect(effect) } }
        }
    }
    override fun initView() {
        initSearchBar()
        initSpotCategory()
        initTodayRecommendedSpotView()
        initHorizontalCardView()
        initCategoryTab()
    }

    private fun updateUI(state: HomeState) {
        state.selectedCategory?.let { configureSelectedSpotCategory(it) }

        binding.layoutCategoryRecommendMore.isVisible = state.categorySpots.size >= 3
        binding.layoutCategoryTabNoResult.isVisible = state.categorySpots.isEmpty()

        viewLifecycleOwner.lifecycleScope.launch {
            categoryRecommendAdapter.setItems(state.categorySpots)
        }
    }
    private fun handleSideEffect(effect: HomeSideEffect) {
        when (effect) {
            is HomeSideEffect.NavigateTodayRecommendedSpot -> { navigateTodaySpotRecommended() }
            is HomeSideEffect.NavigateSpotDetail -> { navigateSpotDetail(effect.spotId) }
            is HomeSideEffect.NavigateSearch -> { navigateSearch() }
            is HomeSideEffect.NavigateCategorySpot -> {  navigateCategorySpot(effect.category) }
        }
    }


    private fun initTodayRecommendedSpotView() {
        val spotList = listOf(
            SpotPolaroidView.SpotPolaroidItem(
                title = "마포대교 북단 중앙로",
                location = "서울시 마포구",
                imageResId = com.cmc.design.R.drawable.img_sample,
                tags = listOf("#야경맛집", "#사진찍기좋아요"),
                isScraped = false,
                isRecommended = true
            ),
            SpotPolaroidView.SpotPolaroidItem(
                title = "효사정공원 3번 쉼터    ",
                location = "서울시 성동구",
                imageResId = com.cmc.design.R.drawable.img_sample,
                tags = listOf("#공원", "#데이트코스"),
                isScraped = true,
                isRecommended = false
            ),
            SpotPolaroidView.SpotPolaroidItem(
                title = "서울숲 은행나무길",
                location = "서울시 성동구",
                imageResId = com.cmc.design.R.drawable.img_sample,
                tags = listOf("#가을", "#데이트코스"),
                isScraped = false,
                isRecommended = false
            ),
            SpotPolaroidView.SpotPolaroidItem(
                title = "매봉산 꼭대기",
                location = "서울시 성동구",
                imageResId = com.cmc.design.R.drawable.img_sample,
                tags = listOf("#산", "#야경"),
                isScraped = true,
                isRecommended = true
            ),
            SpotPolaroidView.SpotPolaroidItem(
                title = "덕수궁 벚꽃나무 앞",
                location = "서울시 성동구",
                imageResId = com.cmc.design.R.drawable.img_sample,
                tags = listOf("#벚꽃", "#데이트코스"),
                isScraped = false,
                isRecommended = false
            )
        )
        val adapter = SpotPolaroidAdapter(
            spotList,
            onArchiveClick = { spot ->
                // TODO: Scrap Toggle API
                Toast.makeText(context, "${spot.title} 아카이브 클릭됨", Toast.LENGTH_SHORT).show()
            },
            onImageClick = { spot ->
                // 이미지 클릭 시 동작
                Toast.makeText(context, "${spot.title} 이미지 클릭됨", Toast.LENGTH_SHORT).show()
            }
        )

        binding.vpSpotRecommend.setAdapter(adapter)
        binding.tvSpotPolaroidMore.setOnClickListener { viewModel.onClickTodayRecommendedSpotMoreButton() }
    }
    private fun initSearchBar() {
        binding.searchbarHome.
            setOnSearchBarClickListener { viewModel.onClickSearchBar() }
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
            onArchiveClick = { spotId ->
                viewModel.onClickSpotScrapButton(spotId = spotId)
            },
            onImageClick = {
                Toast.makeText(context, "서울숲 은행나무길 카드 클릭됨", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvSpotCategory.apply {
            suppressLayout(false)
            layoutManager = LinearLayoutManager(context)
            this.adapter = categoryRecommendAdapter
        }

        binding.layoutCategoryRecommendMore.setOnClickListener {
            viewModel.onClickCategoryRecommendMoreButton()
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

    private fun navigateTodaySpotRecommended() { navigate(R.id.navigate_today_spot_recommendation) }
    private fun navigateSpotDetail(spotId: Int) { (activity as GlobalNavigation).navigateSpotDetail(spotId) }
    private fun navigateSearch() { (activity as GlobalNavigation).navigateSearch() }
    private fun navigateCategorySpot(category: SpotCategory) {
        navigate(R.id.navigate_category_spots, Bundle().apply {
            putInt(NavigationKeys.Category.ARGUMENT_CATEGORY_ID, category.id)
        })
    }
}