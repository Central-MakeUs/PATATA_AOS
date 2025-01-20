package com.cmc.presentation.search

import android.util.Log
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
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import com.cmc.presentation.search.SearchViewModel.SearchStatus

@AndroidEntryPoint
class SearchFragment: BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {

    private val viewModel: SearchViewModel by viewModels()

    override fun initView() {
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
        with(binding) {
            layoutSearchNoResult.isVisible = false

            if (appbar.getBodyType() == PatataAppBar.BodyType.SEARCH) return

            appbar.setBodyType(PatataAppBar.BodyType.SEARCH)
            appbar.setSearchText(query)
            appbar.background = AppCompatResources.getDrawable(requireContext(), com.cmc.design.R.color.transparent)
            layoutSearchbar.isVisible = false

        }
    }

    private fun showSearchResults(query: String, results: List<TempSpotResult>) {
        with(binding) {
            layoutSearchNoResult.isVisible = false

            if (appbar.getBodyType() == PatataAppBar.BodyType.TITLE) return

            searchbarSecond.setSearchText(query)
            appbar.setBodyType(PatataAppBar.BodyType.TITLE)
            appbar.background = AppCompatResources.getDrawable(requireContext(), com.cmc.design.R.color.white)
            layoutSearchbar.isVisible = true
        }
    }

    private fun showNoResult(query: String) {
        with(binding) {
            tvSearchNoResult.text = getString(com.cmc.design.R.string.search_no_result_text, query)
            layoutSearchNoResult.isVisible = true

            if (appbar.getBodyType() == PatataAppBar.BodyType.SEARCH) return
            appbar.setBodyType(PatataAppBar.BodyType.SEARCH)
            appbar.setSearchText(query)
            appbar.background = AppCompatResources.getDrawable(requireContext(), com.cmc.design.R.color.transparent)
            layoutSearchbar.isVisible = false

        }
    }

    override fun initObserving() {

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state.searchStatus) {
                    SearchStatus.IDLE -> showSearchBarOnly(state.query)
                    SearchStatus.LOADING -> { }
                    SearchStatus.SUCCESS -> showSearchResults(state.query, state.results)
                    SearchStatus.EMPTY -> showNoResult(state.query)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.sideEffect.collect { effect ->
                when (effect) {
                    is SearchViewModel.SearchSideEffect.ShowToast -> {
                        showToast(effect.message)
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}