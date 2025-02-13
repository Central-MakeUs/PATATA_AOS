package com.cmc.presentation.search.view

import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.design.component.BottomSheetDialog
import com.cmc.design.component.PatataAppBar
import com.cmc.domain.model.CategorySortType
import com.cmc.presentation.R
import com.cmc.presentation.databinding.ContentSheetSortSelectBinding
import com.cmc.presentation.databinding.FragmentSearchBinding
import com.cmc.presentation.search.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.cmc.presentation.search.viewmodel.SearchViewModel.SearchStatus
import com.cmc.presentation.search.viewmodel.SearchViewModel.SearchState
import com.cmc.presentation.search.viewmodel.SearchViewModel.SearchSideEffect
import com.cmc.presentation.search.adapter.SpotThumbnailAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment: BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var spotThumbnailAdapter: SpotThumbnailAdapter

    override fun initObserving() {
        repeatWhenUiStarted {
            launch { viewModel.state.collectLatest { state -> updateUI(state) } }
            launch { viewModel.sideEffect.collect { effect -> handleSideEffect(effect) } }
        }
    }
    override fun initView() {
        setAppbar()
        setRecyclerView()
        initSortViewListener()
    }

    private fun updateUI(state: SearchState) {
        updateSortText(state.sortType.text)

        viewLifecycleOwner.lifecycleScope.launch {
            spotThumbnailAdapter.submitData(state.results)
        }

        when (state.searchStatus) {
            SearchStatus.IDLE -> showSearchBarOnly(state.query)
            SearchStatus.LOADING -> { }
            SearchStatus.SUCCESS -> { }
            SearchStatus.EMPTY -> showNoResult(state.query)
            SearchStatus.ERROR -> { }
            SearchStatus.LOADED -> showSearchResults(state.query)
        }
    }
    private fun handleSideEffect(effect: SearchSideEffect) {
        when (effect) {
            is SearchSideEffect.ShowToast -> { showToast(effect.message) }
            is SearchSideEffect.ShowSortDialog -> { showSortDialog() }
            is SearchSideEffect.NavigateSpotDetail -> { navigateSpotDetail(effect.spotId) }
        }
    }

    private fun setAppbar() {
        binding.appbar.focusSearchInput()
        binding.appbar.setupAppBar(
            title = getString(R.string.title_search_content),
            onHeadButtonClick = { finish() },
            onSearch = { text -> viewModel.performSearch(text) }
        )
        binding.searchbarSecond.setOnSearchListener { text ->
            if (binding.appbar.getBodyType() == PatataAppBar.BodyType.TITLE) {
                viewModel.performSearch(text)
            }
        }
    }
    private fun setRecyclerView() {
        spotThumbnailAdapter = SpotThumbnailAdapter(
            onArchiveClick = { spotId -> viewModel.onClickSpotScrapButton(spotId) },
            onImageClick = { spotId -> viewModel.onClickSpotImage(spotId) },
        )
        viewModel.setPageAdapterLoadStateListener(spotThumbnailAdapter)
        binding.rvSpotGrid.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = spotThumbnailAdapter
        }
    }
    private fun initSortViewListener() {
        binding.tvSpotResultSortBy.setOnClickListener {
            viewModel.onClickSearchSortButton()
        }
    }

    private fun updateSearchUI(
        bodyType: PatataAppBar.BodyType,
        query: String,
        isSearchBarVisible: Boolean,
    ) {
        with(binding) {
            appbar.setBodyType(bodyType)
            appbar.changeBackgroundStyle(
                if (bodyType == PatataAppBar.BodyType.TITLE) 1
                else 0
            )
            if (bodyType == PatataAppBar.BodyType.TITLE) {
                searchbarSecond.setSearchText(query)
            } else {
                appbar.setSearchText(query)
            }
            layoutSearchbar.isVisible = isSearchBarVisible
        }
    }
    private fun updateSortText(sortText: String) {
        binding.tvSpotResultSortBy.text = sortText
    }

    private fun showSearchBarOnly(query: String) {
        with(binding) {
            layoutSearchNoResult.isVisible = false
            groupResultView.isVisible = false
            if (appbar.getBodyType() != PatataAppBar.BodyType.SEARCH)
                updateSearchUI(PatataAppBar.BodyType.SEARCH, query, false)
        }
    }
    private fun showSearchResults(query: String) {
        with(binding) {
            layoutSearchNoResult.isVisible = false
            groupResultView.isVisible = true
            if (binding.appbar.getBodyType() != PatataAppBar.BodyType.TITLE)
                updateSearchUI(PatataAppBar.BodyType.TITLE, query, true)
        }
    }
    private fun showNoResult(query: String) {
        with(binding) {
            layoutSearchNoResult.isVisible = true
            groupResultView.isVisible = false
            tvSearchNoResult.text = getString(com.cmc.design.R.string.search_no_result_text, query)
            if (appbar.getBodyType() != PatataAppBar.BodyType.SEARCH)
                updateSearchUI(PatataAppBar.BodyType.SEARCH, query, false)
        }
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

                    val type = viewModel.getSortType()

                    tvSortByDistance.apply {
                        setTextColor(getTextColor(type == CategorySortType.DISTANCE))
                        setOnClickListener {
                            viewModel.onClickSortByDistance()
                            hide()
                        }
                    }

                    tvSortByRecommend.apply {
                        setTextColor(getTextColor(type == CategorySortType.RECOMMEND))
                        setOnClickListener {
                            viewModel.onClickSortByRecommend()
                            hide()
                        }
                    }

                    show()
                }
            }
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateSpotDetail(spotId: Int) { (activity as GlobalNavigation).navigateSpotDetail(spotId) }

}