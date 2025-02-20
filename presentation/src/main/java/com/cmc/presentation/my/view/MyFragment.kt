package com.cmc.presentation.my.view

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.cmc.common.adapter.GridSpaceItemDecoration
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentMyBinding
import com.cmc.presentation.my.adapter.MyRegisteredSpotAdapter
import com.cmc.presentation.my.viewmodel.MyViewModel
import com.cmc.presentation.my.viewmodel.MyViewModel.MySideEffect
import com.cmc.presentation.my.viewmodel.MyViewModel.MyState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyFragment: BaseFragment<FragmentMyBinding>(R.layout.fragment_my) {

    private val viewModel: MyViewModel by viewModels()

    private lateinit var myRegisteredSpotAdapter: MyRegisteredSpotAdapter

    override fun initObserving() {
        repeatWhenUiStarted {
            launch { viewModel.state.collectLatest { state -> updateUI(state) }}
            launch { viewModel.sideEffect.collect { effect -> handleSideEffect(effect) }}
        }
    }
    override fun initView() {
        setAppBar()
        setRecyclerView()
        setButton()
    }

    private fun updateUI(state: MyState) {
        myRegisteredSpotAdapter.setItems(state.spots)
        binding.layoutRegisteredSpotNoResult.isVisible = state.spots.isEmpty()
        binding.tvMyRegisteredSpotsCount.text = state.spots.size.toString() ?: getString(R.string.zero)
    }
    private fun handleSideEffect(effect: MySideEffect) {
        when (effect) {
            is MySideEffect.NavigateToSetting -> {
                // TODO: 설정 화면으로 이동
            }
            is MySideEffect.NavigateToCategorySpots -> { navigateCategorySpot(effect.categoryId) }
            is MySideEffect.NavigateSpotDetail -> { navigateSpotDetail(effect.spotId) }
            is MySideEffect.ShowToast -> {}
        }
    }

    private fun setAppBar() {
        binding.settingAppbar.apply {
            setupAppBar(
                title = getString(R.string.title_my_profile),
                onFootButtonClick = { viewModel.onClickSettingButton() },
            )
        }
    }
    private fun setRecyclerView() {
        myRegisteredSpotAdapter = MyRegisteredSpotAdapter(onImageClick = { viewModel.onClickSpotImage(it) })
        binding.rvSetting.apply {
            layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT)
            adapter = myRegisteredSpotAdapter
            addItemDecoration(GridSpaceItemDecoration(SPAN_COUNT, GRID_SPACE))
        }
    }
    private fun setButton() {
        binding.layoutExploreSpotButton.setOnClickListener {
            viewModel.onClickExploreSpotButton()
        }
    }

    private fun navigateSpotDetail(spotId: Int) { (activity as GlobalNavigation).navigateSpotDetail(spotId) }
    private fun navigateCategorySpot(categoryId: Int) {
        (activity as GlobalNavigation).navigateCategorySpots(categoryId)
    }

    companion object {
        private const val SPAN_COUNT = 2
        private const val GRID_SPACE = 12 // 아이템 간격 (px)
    }
}