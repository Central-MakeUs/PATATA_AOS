package com.cmc.presentation.search

import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.cmc.common.base.BaseFragment
import com.cmc.design.component.PatataAppBar
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentSearchBinding
import com.cmc.presentation.search.SearchViewModel.TempSpotResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.cmc.presentation.search.SearchViewModel.SearchStatus

@AndroidEntryPoint
class SearchFragment: BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {

    private val viewModel: SearchViewModel by viewModels()

    override fun initView() {
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

    private fun showSearchBarOnly(query: String) {
        binding.layoutSearchNoResult.isVisible = false
        if (binding.appbar.getBodyType() != PatataAppBar.BodyType.SEARCH)
            updateSearchUI(PatataAppBar.BodyType.SEARCH, query, false, com.cmc.design.R.color.transparent)
    }

    private fun showSearchResults(query: String, results: List<TempSpotResult>) {
        binding.layoutSearchNoResult.isVisible = false
        if (binding.appbar.getBodyType() != PatataAppBar.BodyType.TITLE)
            updateSearchUI(PatataAppBar.BodyType.TITLE, query, true, com.cmc.design.R.color.white)
    }

    private fun showNoResult(query: String) {
        binding.layoutSearchNoResult.isVisible = true
        binding.tvSearchNoResult.text = getString(com.cmc.design.R.string.search_no_result_text, query)
        if (binding.appbar.getBodyType() != PatataAppBar.BodyType.SEARCH)
            updateSearchUI(PatataAppBar.BodyType.SEARCH, query, false, com.cmc.design.R.color.transparent)
    }

    override fun initObserving() {
        repeatWhenUiStarted {
            viewModel.state.collect { state ->
                when (state.searchStatus) {
                    SearchStatus.IDLE -> showSearchBarOnly(state.query)
                    SearchStatus.LOADING -> { }
                    SearchStatus.SUCCESS -> showSearchResults(state.query, state.results)
                    SearchStatus.EMPTY -> showNoResult(state.query)
                }
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