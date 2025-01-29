package com.cmc.presentation.map

import com.cmc.common.base.BaseFragment
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentSpotAddedSuccessBinding

class SpotAddedSuccessFragment: BaseFragment<FragmentSpotAddedSuccessBinding>(R.layout.fragment_spot_added_success) {
    override fun initObserving() {

    }

    override fun initView() {
        binding.layoutConfirmButton.setOnClickListener {
            navigate(R.id.navigate_around_me)
        }
    }
}