package com.cmc.presentation.spot

import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmc.common.base.BaseFragment
import com.cmc.common.constants.NavigationKeys
import com.cmc.design.component.BottomSheetDialog
import com.cmc.presentation.R
import com.cmc.presentation.databinding.ContentSheetSpotDetailComplaintBinding
import com.cmc.presentation.databinding.ContentSheetSpotDetailMoreBinding
import com.cmc.presentation.databinding.FragmentSpotDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.cmc.presentation.spot.SpotDetailViewModel.SpotDetailSideEffect

@AndroidEntryPoint
class SpotDetailFragment: BaseFragment<FragmentSpotDetailBinding>(R.layout.fragment_spot_detail) {

    private val viewModel: SpotDetailViewModel by viewModels()

    override fun initObserving() {
        repeatWhenUiStarted {
            launch {
                viewModel.state.collectLatest {

                }
            }

            launch {
                viewModel.sideEffect.collectLatest { s ->
                    when (s) {
                        is SpotDetailSideEffect.ShowBottomSheet -> {
                            setFooterBottomSheetDialog(s.spotIsMine)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    override fun initView() {

        // TODO: 앱바에 데이터 반영
        setAppBar("", null)
        setViewPager()
        setSpotContent()
        setComment()

//        arguments?.getInt(NavigationKeys.SPOT_DETAIL_ARGUMENT_SPOT_ID)?.let {
//
//        }


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
    ) {
        binding.appbar.setupAppBar(
            title = title,
            titleIconResId,
            onBackClick = { finish() },
            onFootButtonClick = {
                // TODO: 스팟 등록자 확인
                viewModel.clickFooterButton(true)
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

    private fun setComment() {
        setRecyclerView()
//        binding.tvCommentCount.text =
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

    private fun setRecyclerView() {
        val adapter = SpotCommentAdapter(getAdapterDumpData())
        binding.rvComment.apply {
            layoutManager = LinearLayoutManager(context)
            setAdapter(adapter)
        }
    }

    private fun getAdapterDumpData(): List<CommentUiModel> = listOf(
        CommentUiModel("김뿡", "계단을 조금 올라가야돼서 힘들지만, 힘든만큼 보람찬 결과물이 나오네요!", "25.01.23  00:13"),
        CommentUiModel("제리", "일출시간 맞춰서 방문했는데 아주 좋았어요! 미세먼지 많은 날 촬영나갔더니 국회의사당이 흐리게 보여서 조금 아쉬웠지만, 맑은 날 가면 정말 예쁠 스팟이에요", "25.01.23  00:13"),
        CommentUiModel("행", "계단을 조금 올라가야돼서 힘들지만, 힘든만큼 보람찬 결과물이 나오네요!", "25.01.23  00:13"),
        CommentUiModel("짐", "일출시간 맞춰서 방문했는데 아주 좋았어요! 미세먼지 많은 날 촬영나갔더니 국회의사당이 흐리게 보여서 조금 아쉬웠지만, 맑은 날 가면 정말 예쁠 스팟이에요", "25.01.23  00:13"),
        CommentUiModel("멜론", "계단을 조금 올라가야돼서 힘들지만, 힘든만큼 보람찬 결과물이 나오네요!", "25.01.23  00:13"),
        CommentUiModel("도얌", "일출시간 맞춰서 방문했는데 아주 좋았어요! 미세먼지 많은 날 촬영나갔더니 국회의사당이 흐리게 보여서 조금 아쉬웠지만, 맑은 날 가면 정말 예쁠 스팟이에요", "25.01.23  00:13"),
    )
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