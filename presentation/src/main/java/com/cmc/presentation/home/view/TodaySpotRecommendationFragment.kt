package com.cmc.presentation.home.view

import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentTodaySpotRecommendationBinding
import com.cmc.presentation.home.adapter.SpotHorizontalMultiImageCardAdapter
import com.cmc.presentation.home.viewmodel.TodaySpotRecommendationViewModel
import com.cmc.presentation.home.viewmodel.TodaySpotRecommendationViewModel.TodaySpotRecommendationState
import com.cmc.presentation.home.viewmodel.TodaySpotRecommendationViewModel.TodaySpotRecommendSideEffect
import com.cmc.presentation.map.model.TodayRecommendedSpotUiModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TodaySpotRecommendationFragment: BaseFragment<FragmentTodaySpotRecommendationBinding>(R.layout.fragment_today_spot_recommendation) {

    private val viewModel: TodaySpotRecommendationViewModel by viewModels()
    private lateinit var spotRecommendAdapter: SpotHorizontalMultiImageCardAdapter

    override fun initObserving() {
        repeatWhenUiStarted {
            launch { viewModel.state.collect { state -> updateUI(state)} }
            launch { viewModel.sideEffect.collectLatest { effect -> handleSideEffect(effect) } }
        }
    }
    override fun initView() {
        setAppBar()
        startShimmer()
    }

    private fun updateUI(state: TodaySpotRecommendationState) {
        if (::spotRecommendAdapter.isInitialized && state.recommendedSpots.isNotEmpty()) {
            spotRecommendAdapter.setItems(state.recommendedSpots)
            spotRecommendAdapter.notifyDataSetChanged()
        } else if (state.recommendedSpots.isNotEmpty()){
            initTodayRecommendedSpotView(state.recommendedSpots)
        }
    }
    private fun handleSideEffect(effect: TodaySpotRecommendSideEffect) {
        when (effect) {
            is TodaySpotRecommendSideEffect.NavigateSpotDetail -> { navigateSpotDetail(effect.spotId) }
        }
    }

    private fun startShimmer() {
        binding.layoutShimmer.startShimmer()

        viewLifecycleOwner.lifecycleScope.launch {
            delay(1500)
            binding.layoutShimmer.stopShimmer()
            binding.layoutShimmer.isVisible = false
            binding.rvTodaySpotRecommendation.isVisible = true
        }
    }
    private fun setAppBar() {
        binding.appbarTodaySpotRecommendation.setupAppBar(
            title = getString(R.string.title_today_spot_recommend),
            onHeadButtonClick = { finish() }
        )
    }

    private fun initTodayRecommendedSpotView(items: List<TodayRecommendedSpotUiModel>) {
        spotRecommendAdapter = SpotHorizontalMultiImageCardAdapter(
            items = items,
            onImageClick = { viewModel.onClickSpotImage(it) },
            onArchiveClick = { viewModel.onClickSpotScrapButton(it) }
        )
        binding.rvTodaySpotRecommendation.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = spotRecommendAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        if (::spotRecommendAdapter.isInitialized) {
            binding.rvTodaySpotRecommendation.apply {
                layoutManager = LinearLayoutManager(context)
                this.adapter = spotRecommendAdapter
            }
        }
        viewModel.refreshHomeScreen()
    }

    private fun navigateSpotDetail(spotId: Int) {
        (activity as GlobalNavigation).navigateSpotDetail(spotId)
    }
}