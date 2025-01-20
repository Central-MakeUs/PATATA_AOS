package com.cmc.presentation.home

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.forEachIndexed
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.design.component.PatataAppBar
import com.cmc.design.component.SpotHorizontalCardView
import com.cmc.design.component.SpotPolaroidView
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment: BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var categoryViews: List<Pair<View, SpotCategory>>

    override fun initView() {

        // 오늘의 추천 스팟, 폴라로이드 뷰
        val spotList = listOf(
            SpotPolaroidView.SpotPolaroidItem(
                title = "마포대교 북단 중앙로",
                location = "서울시 마포구",
                imageResId = com.cmc.design.R.drawable.img_sample,
                tags = listOf("#야경맛집", "#사진찍기좋아요"),
                isArchived = false,
                isRecommended = true
            ),
            SpotPolaroidView.SpotPolaroidItem(
                title = "효사정공원 3번 쉼터    ",
                location = "서울시 성동구",
                imageResId = com.cmc.design.R.drawable.img_sample,
                tags = listOf("#공원", "#데이트코스"),
                isArchived = true,
                isRecommended = false
            ),
            SpotPolaroidView.SpotPolaroidItem(
                title = "서울숲 은행나무길",
                location = "서울시 성동구",
                imageResId = com.cmc.design.R.drawable.img_sample,
                tags = listOf("#가을", "#데이트코스"),
                isArchived = false,
                isRecommended = false
            ),
            SpotPolaroidView.SpotPolaroidItem(
                title = "매봉산 꼭대기",
                location = "서울시 성동구",
                imageResId = com.cmc.design.R.drawable.img_sample,
                tags = listOf("#산", "#야경"),
                isArchived = true,
                isRecommended = true
            ),
            SpotPolaroidView.SpotPolaroidItem(
                title = "덕수궁 벚꽃나무 앞",
                location = "서울시 성동구",
                imageResId = com.cmc.design.R.drawable.img_sample,
                tags = listOf("#벚꽃", "#데이트코스"),
                isArchived = false,
                isRecommended = false
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

        // 앱 바
        binding.appbar.setupAppBar(
            title = "테스트 화면2",
            icon = com.cmc.design.R.drawable.ic_spot_location,
            iconPosition = PatataAppBar.IconPosition.START,
            onBackClick = { requireActivity().onBackPressedDispatcher.onBackPressed() },
            onHeadButtonClick = { Toast.makeText(context, "헤드 버튼 클릭!", Toast.LENGTH_SHORT).show()},
            onFootButtonClick = { Toast.makeText(context, "푸터 버튼 클릭!", Toast.LENGTH_SHORT).show()}
        )
        // 서치 바
        binding.searchbarHome.apply {
            setOnSearchBarClickListener {
                (activity as GlobalNavigation).navigateSearch()
            }
            setOnSearchListener { str ->
                Toast.makeText(context, "검색 성공 $str", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvSpotPolaroidMore.setOnClickListener {
            Toast.makeText(context, "오늘의 추천 스팟으로 이동!", Toast.LENGTH_SHORT).show()
        }
        
        // 스팟 카테고리
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

        // 카테고리별 추천
        val spotCardList = listOf(
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
            )
        )

        val categoryRecommendAdapter = CategoryRecommendAdapter(
            spotCardList,
            onArchiveClick = {
                Toast.makeText(context, "서울숲 은행나무길 아카이브 클릭됨", Toast.LENGTH_SHORT).show()
            },
            onImageClick = {
                Toast.makeText(context, "서울숲 은행나무길 카드 클릭됨", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvSpotCategory.apply {
            suppressLayout(false)
            layoutManager = LinearLayoutManager(context)
            this.adapter = categoryRecommendAdapter
        }

        // TabLayout
        val categoryTabs = listOf("전체", "작가 추천", "스냅 스팟", "시크한 야경", "일상 속 공간")
        categoryTabs.forEach { category ->
            binding.tabCategoryFilter.addTab(
                binding.tabCategoryFilter.newTab().setText(category)
            )
        }

        val tabs = binding.tabCategoryFilter.getChildAt(0) as ViewGroup
        tabs.forEachIndexed { index, view ->
            val layoutParams = view.layoutParams as LinearLayout.LayoutParams
            layoutParams.setMargins(10, 0, 10, 0)
        }
        binding.tabCategoryFilter.requestLayout()

        // 탭 선택 이벤트
        binding.tabCategoryFilter.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    // TODO: Filter 동작
                    Toast.makeText(context, "${it.text} Chip Click!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
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