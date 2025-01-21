package com.cmc.presentation.search

import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.cmc.common.base.BaseFragment
import com.cmc.design.component.PatataAppBar
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import com.cmc.presentation.search.SearchViewModel.SearchStatus
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment: BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var spotThumbnailAdapter: SpotThumbnailAdapter

    override fun initObserving() {
        repeatWhenUiStarted {
            viewModel.state.collectLatest { state ->
                when (state.searchStatus) {
                    SearchStatus.IDLE -> showSearchBarOnly(state.query)
                    SearchStatus.LOADING -> { }
                    SearchStatus.SUCCESS -> { }
                    SearchStatus.EMPTY -> {
                        showNoResult(state.query)
                    }
                    SearchStatus.ERROR -> { }
                    SearchStatus.LOADED -> {
                        showSearchResults(state.query)
                    }
                }
                spotThumbnailAdapter.submitData(state.results)
            }
        }

        repeatWhenUiStarted {
            viewModel.sideEffect.collect { effect ->
                when (effect) {
                    is SearchViewModel.SearchSideEffect.ShowToast -> {
                        showToast(effect.message)
                    }
                }
            }
        }
    }

    override fun initView() {
        setAppbar()
        setRecyclerView()
    }

    private fun setAppbar() {
        binding.appbar.focusSearchInput()
        binding.appbar.setupAppBar(
            title = getString(com.cmc.design.R.string.title_search_content),
            onBackClick = { requireActivity().onBackPressedDispatcher.onBackPressed() },
            onSearch = { text ->
                viewModel.performSearch(text)
            }
        )
        binding.searchbarSecond.setOnSearchListener { text ->
            if (binding.appbar.getBodyType() == PatataAppBar.BodyType.TITLE) {
                viewModel.performSearch(text)
            }
        }
    }

    private fun setRecyclerView() {
        spotThumbnailAdapter = SpotThumbnailAdapter(
            onArchiveClick = { viewModel.clickToastBtn("Archive Click!") },
            onImageClick = { viewModel.clickToastBtn("Image Click!") },
        )
        viewModel.setPageAdapterLoadStateListener(spotThumbnailAdapter)
        binding.rvSpotGrid.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = spotThumbnailAdapter
        }
    }

    private fun showSearchBarOnly(query: String) {
        with(binding) {
            layoutSearchNoResult.isVisible = false
            groupResultView.isVisible = false
            if (appbar.getBodyType() != PatataAppBar.BodyType.SEARCH)
                updateSearchUI(PatataAppBar.BodyType.SEARCH, query, false, com.cmc.design.R.color.transparent)
        }
    }

    private fun showSearchResults(query: String) {
        with(binding) {
            layoutSearchNoResult.isVisible = false
            groupResultView.isVisible = true
            if (binding.appbar.getBodyType() != PatataAppBar.BodyType.TITLE)
                updateSearchUI(PatataAppBar.BodyType.TITLE, query, true, com.cmc.design.R.color.white)
        }
    }

    private fun showNoResult(query: String) {
        with(binding) {
            layoutSearchNoResult.isVisible = true
            groupResultView.isVisible = false
            tvSearchNoResult.text = getString(com.cmc.design.R.string.search_no_result_text, query)
            if (appbar.getBodyType() != PatataAppBar.BodyType.SEARCH)
                updateSearchUI(PatataAppBar.BodyType.SEARCH, query, false, com.cmc.design.R.color.transparent)
        }
    }

    private fun updateSearchUI(
        bodyType: PatataAppBar.BodyType,
        query: String,
        isSearchBarVisible: Boolean,
        backgroundResId: Int
    ) {
        with(binding) {
            appbar.setBodyType(bodyType)
            if (bodyType == PatataAppBar.BodyType.TITLE) {
                searchbarSecond.setSearchText(query)
            } else {
                appbar.setSearchText(query)
            }
            appbar.background = AppCompatResources.getDrawable(requireContext(), backgroundResId)
            layoutSearchbar.isVisible = isSearchBarVisible
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}