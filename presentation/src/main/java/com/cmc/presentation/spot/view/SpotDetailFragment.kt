package com.cmc.presentation.spot.view

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.common.constants.NavigationKeys
import com.cmc.common.util.ClipboardUtil
import com.cmc.design.component.BottomSheetDialog
import com.cmc.design.component.PatataAlert
import com.cmc.design.util.SnackBarUtil
import com.cmc.design.util.animateClickEffect
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.databinding.ContentSheetSpotDetailComplaintBinding
import com.cmc.presentation.databinding.ContentSheetSpotDetailMoreBinding
import com.cmc.presentation.databinding.FragmentSpotDetailBinding
import com.cmc.presentation.model.SpotCategoryItem
import com.cmc.presentation.spot.adapter.ImageSliderAdapter
import com.cmc.presentation.spot.adapter.SpotReviewAdapter
import com.cmc.presentation.spot.viewmodel.SpotDetailViewModel
import com.cmc.presentation.spot.viewmodel.SpotDetailViewModel.SpotDetailSideEffect
import com.cmc.presentation.spot.viewmodel.SpotDetailViewModel.SpotDetailState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SpotDetailFragment: BaseFragment<FragmentSpotDetailBinding>(R.layout.fragment_spot_detail) {

    private val viewModel: SpotDetailViewModel by viewModels()

    private lateinit var imageSliderAdapter: ImageSliderAdapter
    private lateinit var spotReviewAdapter: SpotReviewAdapter

    private var dialog: BottomSheetDialog? = null

    override fun initObserving() {
        repeatWhenUiStarted {
            launch { viewModel.state.collect { state -> updateUI(state) } }
            launch { viewModel.sideEffect.collectLatest { effect -> handleSideEffect(effect) } }
        }
    }
    override fun initView() {
        initData(arguments)
        setViewPager()
        setClickListener()
        setReviewRecyclerView()
        setEditor()
    }

    private fun updateUI(state: SpotDetailState) {
        state.spotDetail.let {
            val categoryItem = SpotCategoryItem(
                SpotCategory.fromId(it.categoryId) ?: SpotCategory.RECOMMEND
            )
            setAppBar(
                getString(categoryItem.getName()),
                categoryItem.getIcon(),
                it.isAuthor
            )

            imageSliderAdapter.setItems(it.images) { binding.dotsIndicator.updateDotCount() }
            spotReviewAdapter.setItems(it.reviews.toList())

            with(binding) {
                tvSpotTitle.text = it.spotName
                tvSpotPoster.text = it.authorName
                tvSpotLocation.text = it.address
                tvSpotDescription.text = it.description
                updateTags(it.tags)
                setCommentCount(it.reviewCount)
                ivSpotArchive.isSelected = it.isScraped
                binding.viewSpotBadge.root.isVisible = SpotCategory.isRecommended(it.categoryId)
            }
        }
    }
    private fun handleSideEffect(effect: SpotDetailSideEffect) {
        when (effect) {
            is SpotDetailSideEffect.Finish -> { finish() }
            is SpotDetailSideEffect.ShowAlert -> { showAlert() }
            is SpotDetailSideEffect.CopyClipboard -> { copyClipboard(effect.text) }
            is SpotDetailSideEffect.ShowBottomSheet -> { setFooterBottomSheetDialog(effect.spotIsMine) }
            is SpotDetailSideEffect.ShowSnackBar -> { showSnackBar(effect.message) }
            is SpotDetailSideEffect.NavigateReport -> { navigateReport(effect.reportType, effect.targetId) }
            is SpotDetailSideEffect.NavigateEditSpot -> { navigateEditSpot(effect.spotId) }
        }
    }

    private fun initData(bundle: Bundle?) {
        val spotId = bundle?.getInt(NavigationKeys.SpotDetail.ARGUMENT_SPOT_ID) ?: -1
        viewModel.getSpotDetail(spotId)
    }

    private fun setViewPager() {
        imageSliderAdapter = ImageSliderAdapter()

        val viewPager = binding.viewPager
        viewPager.adapter = imageSliderAdapter
        binding.dotsIndicator.attachTo(viewPager)

        binding.viewSpotBadge.viewCornerFold.isVisible = false
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

    private fun updateTags(tags: List<String>?) {
        binding.layoutTag.removeAllViews()

        tags?.forEach { tag ->
            val tagView = LayoutInflater.from(context).inflate(com.cmc.design.R.layout.view_tag_blue, binding.layoutTag, false)
            "#$tag".also { tagView.findViewById<TextView>(com.cmc.design.R.id.tv_tag).text = it }

            binding.layoutTag.addView(tagView)
        }
    }
    private fun setClickListener() {
        with(binding) {
            ivSpotArchive.setOnClickListener {
                ivSpotArchive.animateClickEffect()
                viewModel.onClickScrapButton()
            }
            tvSpotLocationCopy.setOnClickListener {
                viewModel.onClickLocationCopyButton()
            }
        }
    }
    private fun setCommentCount(reviewCount: Int) {
        binding.tvReviewCount.text = reviewCount.toString() ?: getString(R.string.zero)
    }
    private fun setFooterBottomSheetDialog(postIsMine: Boolean) {
        dialog = if (postIsMine) {
            BottomSheetDialog(requireContext())
                .bindBuilder(
                    ContentSheetSpotDetailMoreBinding.inflate(LayoutInflater.from(requireContext()))
                ) { dialog ->
                    with(dialog) {
                        tvEditPost.setOnClickListener {
                            dismiss()
                            viewModel.onClickEditSpot()
                        }
                        tvDelete.setOnClickListener {
                            dismiss()
                            viewModel.onClickDeleteSpot()
                        }
                        show()
                    }
                }
        } else {
            BottomSheetDialog(requireContext())
                .bindBuilder(
                    ContentSheetSpotDetailComplaintBinding.inflate(LayoutInflater.from(requireContext()))
                ) { dialog ->
                    with(dialog) {
                        tvComplaintPost.setOnClickListener {
                            dismiss()
                            viewModel.onClickPostReport()
                        }
                        tvComplaintUser.setOnClickListener {
                            dismiss()
                            viewModel.onClickUserReport()
                        }
                        show()
                    }
                }
        }
    }
    private fun setReviewRecyclerView() {
        spotReviewAdapter = SpotReviewAdapter { reviewId -> viewModel.onClickReviewDelete(reviewId) }
        binding.rvReview.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = spotReviewAdapter
            setHasFixedSize(true)
        }
    }
    private fun setEditor() {
        binding.editorInput.setOnSubmitListener { text ->
            viewModel.submitReviewEditor(text)
            binding.editorInput.clearEditor()
        }
    }

    private fun showAlert() {
        PatataAlert(requireContext())
            .title(getString(R.string.dialog_post_delete_title))
            .content(getString(R.string.dialog_post_delete_description))
            .multiButton {
                leftButton(getString(R.string.cancel))
                rightButton(getString(R.string.delete)) {
                    viewModel.onClickDeleteSpot()
                }
            }
    }
    private fun showSnackBar(message: String) {
        SnackBarUtil.show(binding.root, message)
    }
    private fun copyClipboard(text: String) {
        ClipboardUtil.copyText(requireContext(), getString(R.string.label_copy_clipboard), text)
    }

    private fun navigateReport(reportType: Int, targetId: Int) {
        (activity as GlobalNavigation).navigateReport(reportType, targetId)
    }
    private fun navigateEditSpot(spotId: Int) {
        navigate(R.id.navigate_edit_spot, Bundle().apply {
            putInt(NavigationKeys.SpotDetail.ARGUMENT_SPOT_ID, spotId)
        })
    }
    override fun onStop() {
        super.onStop()
        dialog?.dismiss()
    }
}