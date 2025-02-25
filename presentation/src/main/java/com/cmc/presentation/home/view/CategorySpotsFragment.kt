package com.cmc.presentation.home.view

import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.common.constants.NavigationKeys
import com.cmc.design.component.BottomSheetDialog
import com.cmc.domain.model.CategorySortType
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.databinding.ContentSheetSortSelectBinding
import com.cmc.presentation.databinding.FragmentCategorySpotsBinding
import com.cmc.presentation.home.adapter.SpotHorizontalPaginatedCardAdapter
import com.cmc.presentation.home.viewmodel.CategorySpotsViewModel
import com.cmc.presentation.home.viewmodel.CategorySpotsViewModel.CategorySpotsSideEffect
import com.cmc.presentation.home.viewmodel.CategorySpotsViewModel.CategorySpotsState
import com.cmc.presentation.model.SpotCategoryItem
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategorySpotsFragment: BaseFragment<FragmentCategorySpotsBinding>(R.layout.fragment_category_spots) {

    private val viewModel: CategorySpotsViewModel by viewModels()

    private lateinit var categoryRecommendAdapter: SpotHorizontalPaginatedCardAdapter


    override fun initObserving() {
        repeatWhenUiStarted {
            launch { viewModel.state.collect { state -> updateUI(state) } }
            launch { viewModel.sideEffect.collectLatest { effect -> handleSideEffect(effect) } }
        }
    }
    override fun initView() {
        val categoryType = arguments?.getInt(
            NavigationKeys.Category.ARGUMENT_CATEGORY_ID,
            SpotCategory.ALL.id
        )

        startShimmer()
        initAppBar()
        initCategoryTab(categoryType)
        initHorizontalCardView()
        initSortViewListener()
    }

    private fun updateUI(state: CategorySpotsState) {
        if (state.isLoading.not()) {
            viewLifecycleOwner.lifecycleScope.launch {
                binding.layoutShimmer.stopShimmer()
                binding.layoutShimmer.isVisible = false
                binding.groupContents.isVisible = true

                if (state.isLoading.not())
                    categoryRecommendAdapter.submitData(state.categorySpots)
            }
        }
        binding.layoutCategorySpotsNoResult.isVisible = state.isLoading.not() && state.spotCount == 0
        setSort(state.sortType)
        setSpotCount(state.spotCount)
    }
    private fun handleSideEffect(effect: CategorySpotsSideEffect) {
        when (effect) {
            is CategorySpotsSideEffect.NavigateSpotDetail -> { navigateSpotDetail(effect.spotId) }
            is CategorySpotsSideEffect.ShowSortDialog -> { showSortDialog() }
        }
    }

    private fun startShimmer() {
        binding.layoutShimmer.startShimmer()
    }
    private fun initAppBar() {
        binding.categorySpotsAppbar.setupAppBar(
            title = getString(R.string.category),
            onHeadButtonClick = { finish() }
        )
    }
    private fun initSortViewListener() {
        binding.tvCategorySort.setOnClickListener {
            viewModel.onClickCategorySortButton()
        }
    }
    private fun initHorizontalCardView() {
        categoryRecommendAdapter = SpotHorizontalPaginatedCardAdapter(
            onArchiveClick = { spotId -> viewModel.onClickSpotScrapButton(spotId = spotId) },
            onImageClick = { spotId -> viewModel.onClickSpotImage(spotId) }
        )

        viewModel.setPageAdapterLoadStateListener(categoryRecommendAdapter)

        binding.rvSpotCategory.apply {
            suppressLayout(false)
            layoutManager = LinearLayoutManager(context)
            this.adapter = categoryRecommendAdapter
        }
    }
    private fun initCategoryTab(categoryType: Int?) {
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

        // 초기 탭 설정
        binding.tabCategoryFilter.getTabAt(categoryType ?: SpotCategory.ALL.id)
            ?.select()
        viewModel.onClickCategoryTab(SpotCategory.fromId(categoryType ?: SpotCategory.ALL.id))

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

    private fun setSpotCount(count: Int) {
        val str = if (count >= 0) count.toString() else ""
        str.also { binding.tvCategoryCount.text = it }
    }
    private fun setSort(sortType: CategorySortType) {
        binding.tvCategorySort.text = sortType.text
    }

    private fun showSortDialog() {
        BottomSheetDialog(requireContext())
            .bindBuilder(
                ContentSheetSortSelectBinding.inflate(LayoutInflater.from(requireContext()))
            ) { dialog ->
                with(dialog) {
                    fun getTextColor(isSelected: Boolean): Int {
                        val colorId = if (isSelected) com.cmc.design.R.color.black else com.cmc.design.R.color.text_disabled
                        return context.getColor(colorId)
                    }

                    val type = viewModel.state.value.sortType

                    tvSortByDistance.apply {
                        setTextColor(getTextColor(type == CategorySortType.DISTANCE))
                        setOnClickListener {
                            viewModel.onClickSortByDistance()
                            dismiss()
                        }
                    }

                    tvSortByRecommend.apply {
                        setTextColor(getTextColor(type == CategorySortType.RECOMMEND))
                        setOnClickListener {
                            viewModel.onClickSortByRecommend()
                            dismiss()
                        }
                    }
                    show()
                }
            }
    }
    private fun navigateSpotDetail(spotId: Int) { (activity as GlobalNavigation).navigateSpotDetail(spotId) }
}