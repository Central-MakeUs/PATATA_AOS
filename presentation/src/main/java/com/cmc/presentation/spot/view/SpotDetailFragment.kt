package com.cmc.presentation.spot.view

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmc.common.base.BaseFragment
import com.cmc.common.constants.NavigationKeys
import com.cmc.design.component.BottomSheetDialog
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.databinding.ContentSheetSpotDetailComplaintBinding
import com.cmc.presentation.databinding.ContentSheetSpotDetailMoreBinding
import com.cmc.presentation.databinding.FragmentSpotDetailBinding
import com.cmc.presentation.model.SpotCategoryItem
import com.cmc.presentation.spot.viewmodel.SpotDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.cmc.presentation.spot.viewmodel.SpotDetailViewModel.SpotDetailSideEffect
import com.cmc.presentation.spot.viewmodel.SpotDetailViewModel.SpotDetailState
import com.cmc.presentation.spot.adapter.ImageSliderAdapter
import com.cmc.presentation.spot.adapter.SpotCommentAdapter
import com.cmc.presentation.spot.model.ImageViewPagerModel

@AndroidEntryPoint
class SpotDetailFragment: BaseFragment<FragmentSpotDetailBinding>(R.layout.fragment_spot_detail) {

    private val viewModel: SpotDetailViewModel by viewModels()

    override fun initObserving() {
        repeatWhenUiStarted {
            launch { viewModel.state.collect { state -> updateUI(state) } }
            launch { viewModel.sideEffect.collectLatest { effect -> handleSideEffect(effect) } }
        }
    }

    private fun updateUI(state: SpotDetailState) {
        state.spotDetail?.let {
            val categoryItem = SpotCategoryItem(
                SpotCategory.fromId(it.categoryId) ?: SpotCategory.ALL
            )
            setAppBar(
                getString(categoryItem.getName()),
                categoryItem.getIcon(),
                it.isAuthor
            )

            with(binding) {
                tvSpotTitle.text = it.spotName
                tvSpotPoster.text = it.authorName
                tvSpotLocation.text = it.address
                tvSpotDescription.text = it.description
                updateTags(it.tags)
                setCommentCount(it.reviewCount)
            }
        }

    }

    private fun updateTags(tags: List<String>?) {
        binding.layoutTag.removeAllViews()

        tags?.forEach { tag ->
            val tagView = LayoutInflater.from(context).inflate(com.cmc.design.R.layout.view_tag_blue, binding.layoutTag, false)
            val tagTextView = tagView.findViewById<TextView>(com.cmc.design.R.id.tv_tag)
            tagTextView.text = tag

            binding.layoutTag.addView(tagView)
        }
    }

    private fun handleSideEffect(effect: SpotDetailSideEffect) {

    }

    override fun initView() {
        initData(arguments)
        setViewPager()
        setSpotContent()
        setReviewRecyclerView()
    }

    private fun initData(bundle: Bundle?) {
        val spotId = bundle?.getInt(NavigationKeys.SpotDetail.ARGUMENT_SPOT_ID) ?: -1
        viewModel.getSpotDetail(spotId)
    }

    private fun setViewPager() {
        val adapter = ImageSliderAdapter(getViewPagerDumpData())
        val viewPager = binding.viewPager
        viewPager.adapter = adapter
        binding.dotsIndicator.attachTo(viewPager)
    }

    private fun setAppBar(
        title: String,
        titleIconResId: Int? = null,
        isAuthor: Boolean,
    ) {
        binding.appbar.setupAppBar(
            title = title,
            titleIconResId,
            onHeadButtonClick = { finish() },
            onFootButtonClick = {
                viewModel.clickFooterButton(isAuthor)
            },
        )
    }

    private fun setSpotContent() {

        binding.ivSpotArchive.apply {
//            isSelected = false
            setOnClickListener { isSelected = isSelected.not() }
            // TODO: ViewModel에 이벤트 전달
        }

        binding.tvSpotLocationCopy.setOnClickListener { 
            // TODO: 주소 클립보드에 복사
        }
    }

    private fun setCommentCount(reviewCount: Int) {
        binding.tvReviewCount.text = reviewCount.toString() ?: getString(R.string.zero)
    }

    private fun setFooterBottomSheetDialog(postIsMine: Boolean) {
        if (postIsMine) {
            BottomSheetDialog(requireContext())
                .bindBuilder(
                    ContentSheetSpotDetailComplaintBinding.inflate(LayoutInflater.from(requireContext()))
                ) { dialog ->
                    with(dialog) {
                        // TODO: 게시글 신고, 유저 신고 동작 구현
                        show()
                    }
                }
        } else {
            BottomSheetDialog(requireContext())
                .bindBuilder(
                    ContentSheetSpotDetailMoreBinding.inflate(LayoutInflater.from(requireContext()))
                ) { dialog ->
                    with(dialog) {
                        // TODO: 게시글 수정/삭제 동작 구현
                        show()
                    }
                }
        }
    }

    private fun setReviewRecyclerView() {
        val adapter = SpotCommentAdapter()
        binding.rvReview.apply {
            layoutManager = LinearLayoutManager(context)
            setAdapter(adapter)
        }
    }

    private fun getViewPagerDumpData(): List<ImageViewPagerModel> = listOf(
        ImageViewPagerModel(
            url = "https://example.com/image1.jpg",
            isRecommended = true,
            contact = "@photo_spot_1",
            contactResId = com.cmc.design.R.drawable.ic_spot_location
        ),
        ImageViewPagerModel(
            url = "https://example.com/image2.jpg",
            isRecommended = false,
            contact = "@beautiful_places",
            contactResId = com.cmc.design.R.drawable.ic_spot_location
        ),
        ImageViewPagerModel(
            url = "https://example.com/image3.jpg",
            isRecommended = true,
            contact = "@travel_moments",
            contactResId = com.cmc.design.R.drawable.ic_spot_location
        ),
        ImageViewPagerModel(
            url = "https://example.com/image4.jpg",
            isRecommended = false,
            contact = "@hidden_gems",
            contactResId = com.cmc.design.R.drawable.ic_spot_location
        )
    )

}