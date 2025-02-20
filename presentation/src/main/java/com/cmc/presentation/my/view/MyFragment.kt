package com.cmc.presentation.my.view

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.cmc.common.adapter.GridSpaceItemDecoration
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.common.constants.NavigationKeys
import com.cmc.design.util.SnackBarUtil
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
        setProfile(state)
        setRegisteredSpotViewModel(state)
    }
    private fun handleSideEffect(effect: MySideEffect) {
        when (effect) {
            is MySideEffect.NavigateToSetting -> { navigateSetting() }
            is MySideEffect.NavigateToCategorySpots -> { navigateCategorySpot(effect.categoryId) }
            is MySideEffect.NavigateSpotDetail -> { navigateSpotDetail(effect.spotId) }
            is MySideEffect.NavigateSettingProfile -> { navigateSettingProfile(effect.nickName, effect.profileImage) }
            is MySideEffect.ShowSnackBar -> { showSnackBar(effect.message) }
        }
    }

    private fun setProfile(state: MyState) {
        state.profile.let {
            binding.tvNickname.text = it.nickName
            binding.tvEmail.text = it.email
            Glide.with(binding.root).load(it.profileImage).circleCrop().into(binding.ivProfileImage)
        }
    }
    private fun setRegisteredSpotViewModel(state: MyState) {
        myRegisteredSpotAdapter.setItems(state.spots)
        binding.layoutRegisteredSpotNoResult.isVisible = state.spots.isEmpty()
        binding.tvMyRegisteredSpotsCount.text = state.spots.size.toString() ?: getString(R.string.zero)
    }

    private fun setAppBar() {
        binding.myAppbar.apply {
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
        binding.layoutExploreSpotButton.setOnClickListener { viewModel.onClickExploreSpotButton() }
        binding.tvChangeButton.setOnClickListener { viewModel.onClickChangeProfileButton() }
    }


    override fun onResume() {
        super.onResume()
        viewModel.refreshMyPageScreen()
    }

    private fun navigateSpotDetail(spotId: Int) { (activity as GlobalNavigation).navigateSpotDetail(spotId) }
    private fun navigateCategorySpot(categoryId: Int) {
        (activity as GlobalNavigation).navigateCategorySpots(categoryId)
    }
    private fun navigateSettingProfile(nickName: String, profileImage: String) {
        navigate(R.id.navigate_profile_setting, Bundle().apply {
            putString(NavigationKeys.Setting.ARGUMENT_PROFILE_NICKNAME, nickName)
            putString(NavigationKeys.Setting.ARGUMENT_PROFILE_IMAGE, profileImage)
        })
    }
    private fun navigateSetting() { navigate(R.id.navigate_setting) }
    private fun showSnackBar(message: String) { SnackBarUtil.show(binding.root, message) }

    companion object {
        private const val SPAN_COUNT = 2
        private const val GRID_SPACE = 12 // 아이템 간격 (px)
    }
}