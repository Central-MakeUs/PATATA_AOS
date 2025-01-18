package com.cmc.presentation.home

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.cmc.common.base.BaseFragment
import com.cmc.design.component.PatataAppBar
import com.cmc.design.component.SpotPolaroidView
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment: BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var categoryViews: List<Pair<View, SpotCategory>>

    override fun initView() {

        val spotList = listOf(
            SpotPolaroidView.SpotPolaroid(
                title = "마포대교 북단 중앙로",
                location = "서울시 마포구",
                imageResId = com.cmc.design.R.drawable.img_sample,
                tags = listOf("#야경맛집", "#사진찍기좋아요"),
                isArchived = false,
                isBadgeVisible = true
            ),
            SpotPolaroidView.SpotPolaroid(
                title = "효사정공원 3번 쉼터    ",
                location = "서울시 성동구",
                imageResId = com.cmc.design.R.drawable.img_sample,
                tags = listOf("#공원", "#데이트코스"),
                isArchived = true,
                isBadgeVisible = false
            ),
            SpotPolaroidView.SpotPolaroid(
                title = "서울숲 은행나무길",
                location = "서울시 성동구",
                imageResId = com.cmc.design.R.drawable.img_sample,
                tags = listOf("#가을", "#데이트코스"),
                isArchived = false,
                isBadgeVisible = false
            ),
            SpotPolaroidView.SpotPolaroid(
                title = "매봉산 꼭대기",
                location = "서울시 성동구",
                imageResId = com.cmc.design.R.drawable.img_sample,
                tags = listOf("#산", "#야경"),
                isArchived = true,
                isBadgeVisible = true
            ),
            SpotPolaroidView.SpotPolaroid(
                title = "덕수궁 벚꽃나무 앞",
                location = "서울시 성동구",
                imageResId = com.cmc.design.R.drawable.img_sample,
                tags = listOf("#벚꽃", "#데이트코스"),
                isArchived = false,
                isBadgeVisible = false
            )
        )

        val adapter = SpotPolaroidAdapter(
            spotList,
            onArchiveClick = { spot ->
                // 아카이브 버튼 클릭 시 동작
                Toast.makeText(context, "${spot.title} 아카이브 클릭됨", Toast.LENGTH_SHORT).show()
            },
            onImageClick = { spot ->
                // 이미지 클릭 시 동작
                Toast.makeText(context, "${spot.title} 이미지 클릭됨", Toast.LENGTH_SHORT).show()
            }
        )

        binding.vpSpotRecommend.setAdapter(adapter)

        binding.appbar.setupAppBar(
            title = "테스트 화면2",
            icon = com.cmc.design.R.drawable.ic_spot_location,
            iconPosition = PatataAppBar.IconPosition.START,
            onBackClick = { requireActivity().onBackPressedDispatcher.onBackPressed() },
            onHeadButtonClick = { Toast.makeText(context, "헤드 버튼 클릭!", Toast.LENGTH_SHORT).show()},
            onFootButtonClick = { Toast.makeText(context, "푸터 버튼 클릭!", Toast.LENGTH_SHORT).show()}
        )

        binding.searchbarHome.apply {
            setOnSearchBarClickListener {
                Toast.makeText(context, "서치바 클릭!", Toast.LENGTH_SHORT).show()
            }
            setOnSearchListener { str ->
                Toast.makeText(context, "검색 성공 $str", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvSpotPolaroidMore.setOnClickListener {
            Toast.makeText(context, "오늘의 추천 스팟으로 이동!", Toast.LENGTH_SHORT).show()
        }

        categoryViews = listOf(
            binding.viewSpotCategory.layoutCategoryRecommend to SpotCategory.RECOMMEND,
            binding.viewSpotCategory.layoutCategorySnap to SpotCategory.SNAP,
            binding.viewSpotCategory.layoutCategoryNightView to SpotCategory.NIGHT_VIEW,
            binding.viewSpotCategory.layoutCategoryEverydayLife to SpotCategory.EVERYDAY_LIFE,
            binding.viewSpotCategory.layoutCategoryNature to SpotCategory.NATURE
        )

        categoryViews.forEach { (layout, category) ->
            layout.setOnClickListener {
                viewModel.selectCategory(category)
                Toast.makeText(context, "'${category.name}' 카테고리 화면으로 이동!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun initObserving() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedCategory.collectLatest { selectedCategory ->
                categoryViews.forEach { (layout, category) ->
                    layout.isSelected = (category == selectedCategory)
                }
            }
        }
    }
}