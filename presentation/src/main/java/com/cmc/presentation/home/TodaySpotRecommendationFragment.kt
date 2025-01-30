package com.cmc.presentation.home

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmc.common.base.BaseFragment
import com.cmc.design.component.SpotHorizontalCardView
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentTodaySpotRecommendationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodaySpotRecommendationFragment: BaseFragment<FragmentTodaySpotRecommendationBinding>(R.layout.fragment_today_spot_recommendation) {

    private val viewModel: TodaySpotRecommendationViewModel by viewModels()

    override fun initObserving() {
    }

    override fun initView() {

        setAppBar()
        setRecyclerView()
    }

    private fun setAppBar() {
        binding.appbarTodaySpotRecommendation.setupAppBar(
            title = getString(R.string.title_today_spot_recommend),
            onHeadButtonClick = { finish() }
        )
    }

    private fun setRecyclerView() {
        val categoryRecommendAdapter = SpotHorizontalCardAdapter(
            spotCardList,
            onArchiveClick = {
                Toast.makeText(context, "아카이브 클릭됨", Toast.LENGTH_SHORT).show()
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

    private val spotCardList = listOf(
        SpotHorizontalCardView.SpotHorizontalCardItem(
            imageResId = com.cmc.design.R.drawable.img_sample,
            category = "",
            title = "마포대교 북단 중앙로",
            location = "서울시 마포구",
            archiveCount = 117,
            commentCount = 43,
            tags = listOf("#야경맛집", "#사진찍기좋아요"),
            isArchived = true,
            isRecommended = true
        ),
        SpotHorizontalCardView.SpotHorizontalCardItem(
            imageResId = com.cmc.design.R.drawable.img_sample,
            category = "",
            title = "서울숲 은행나무길",
            location = "서울시 성동구",
            archiveCount = 89,
            commentCount = 21,
            tags = listOf("#가을", "#데이트코스"),
            isArchived = false,
            isRecommended = false
        ),
        SpotHorizontalCardView.SpotHorizontalCardItem(
            imageResId = com.cmc.design.R.drawable.img_sample,
            category = "",
            title = "마포대교 북단 중앙로",
            location = "서울시 마포구",
            archiveCount = 117,
            commentCount = 43,
            tags = listOf("#야경맛집", "#사진찍기좋아요"),
            isArchived = true,
            isRecommended = true
        ),
        SpotHorizontalCardView.SpotHorizontalCardItem(
            imageResId = com.cmc.design.R.drawable.img_sample,
            category = "",
            title = "서울숲 은행나무길",
            location = "서울시 성동구",
            archiveCount = 89,
            commentCount = 21,
            tags = listOf("#가을", "#데이트코스"),
            isArchived = false,
            isRecommended = false
        ),
        SpotHorizontalCardView.SpotHorizontalCardItem(
            imageResId = com.cmc.design.R.drawable.img_sample,
            category = "",
            title = "마포대교 북단 중앙로",
            location = "서울시 마포구",
            archiveCount = 117,
            commentCount = 43,
            tags = listOf("#야경맛집", "#사진찍기좋아요"),
            isArchived = true,
            isRecommended = true
        )
    )
}