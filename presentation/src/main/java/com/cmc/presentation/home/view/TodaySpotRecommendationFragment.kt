package com.cmc.presentation.home.view

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmc.common.base.BaseFragment
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentTodaySpotRecommendationBinding
import com.cmc.presentation.home.adapter.SpotHorizontalPaginatedCardAdapter
import com.cmc.presentation.home.viewmodel.TodaySpotRecommendationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodaySpotRecommendationFragment: BaseFragment<FragmentTodaySpotRecommendationBinding>(R.layout.fragment_today_spot_recommendation) {

    private val viewModel: TodaySpotRecommendationViewModel by viewModels()
    private lateinit var categoryRecommendAdapter: SpotHorizontalPaginatedCardAdapter

    override fun initObserving() {
        // TODO: Data Observing
    }
    override fun initView() {
        setAppBar()
        setRecyclerView()
    }

    private fun updateUI() {

    }

    private fun setAppBar() {
        binding.appbarTodaySpotRecommendation.setupAppBar(
            title = getString(R.string.title_today_spot_recommend),
            onHeadButtonClick = { finish() }
        )
    }

    private fun setRecyclerView() {
        categoryRecommendAdapter = SpotHorizontalPaginatedCardAdapter(
            onArchiveClick = {
                Toast.makeText(context, "아카이브 클릭됨", Toast.LENGTH_SHORT).show()
                true
            },
            onImageClick = {
                Toast.makeText(context, "이미지 클릭됨", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvTodaySpotRecommendation.apply {
            suppressLayout(false)
            layoutManager = LinearLayoutManager(context)
            this.adapter = categoryRecommendAdapter
        }
    }
}