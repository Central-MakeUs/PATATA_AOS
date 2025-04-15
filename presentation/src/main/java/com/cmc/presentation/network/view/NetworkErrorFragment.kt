package com.cmc.presentation.network.view

import com.cmc.common.base.BaseFragment
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentNetworkErrorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NetworkErrorFragment : BaseFragment<FragmentNetworkErrorBinding>(R.layout.fragment_network_error){
    override fun initObserving() {
    }

    override fun initView() {
        binding.layoutRetryButton.setOnClickListener {
            if (networkManager.isConnected.value) {
                finish()
            }
        }
    }
}