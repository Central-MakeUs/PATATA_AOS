package com.cmc.presentation.search

import com.cmc.common.base.BaseFragment
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentSearchBinding

class SearchFragment: BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {
    override fun initView() {
        binding.tvSearchNoResult.text = getString(com.cmc.design.R.string.search_no_result_text, "abddsc")
        binding.appbar.setupAppBar(
            onBackClick = { requireActivity().onBackPressedDispatcher.onBackPressed() },
            onSearch = { text ->
                binding.tvSearchNoResult.text = getString(com.cmc.design.R.string.search_no_result_text, text)
            }
        )
    }

    override fun initObserving() {

    }
}