package com.cmc.presentation.archive.view

import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.cmc.common.adapter.GridSpaceItemDecoration
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.design.component.PatataAlert
import com.cmc.design.component.PatataAppBar
import com.cmc.design.component.PatataAppBar.FooterType
import com.cmc.design.util.SnackBarUtil
import com.cmc.presentation.R
import com.cmc.presentation.archive.adapter.ArchivePhotoAdapter
import com.cmc.presentation.archive.viewmodel.ArchiveViewModel
import com.cmc.presentation.archive.viewmodel.ArchiveViewModel.ArchiveSideEffect
import com.cmc.presentation.archive.viewmodel.ArchiveViewModel.ArchiveState
import com.cmc.presentation.databinding.FragmentArchiveBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArchiveFragment: BaseFragment<FragmentArchiveBinding>(R.layout.fragment_archive) {

    private val viewModel: ArchiveViewModel by viewModels()

    private lateinit var archiveAdapter: ArchivePhotoAdapter

    override fun initObserving() {
        repeatWhenUiStarted {
            launch { viewModel.state.collectLatest { state -> updateUI(state) } }
            launch { viewModel.sideEffect.collect { effect -> handleSideEffect(effect) }
            }
        }
    }
    override fun initView() {
        setAppBar()
        setRecyclerView()
        setButton()
    }

    private fun updateUI(state: ArchiveState) {
        if (state.isLoading.not()) {
            binding.layoutArchiveNoResult.isVisible = state.images.isEmpty()
        }

        binding.archiveAppbar.setFooterType(state.footerType)
        archiveAdapter.setItems(state.images, false)
        archiveAdapter.notifyDataSetChanged()
    }
    private fun handleSideEffect(effect: ArchiveSideEffect) {
        when (effect) {
            is ArchiveSideEffect.ShowDeleteImageDialog -> { showDeleteImageDialog(effect.count) }
            is ArchiveSideEffect.ShowSnackbar -> { showSnackBar(effect.message) }
            is ArchiveSideEffect.NavigateSpotDetail -> { navigateSpotDetail(effect.spotId) }
            is ArchiveSideEffect.NavigateToCategorySpots -> { navigateCategorySpot(effect.categoryId) }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.refreshArchiveScreen()
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearState()
    }

    private fun setAppBar() {
        binding.archiveAppbar.apply {
            setupAppBar(
                title = getString(R.string.title_archive),
                onFootButtonClick = { type ->
                    when (type) {
                        PatataAppBar.FooterType.SELECT -> { viewModel.onClickSelectButton() }
                        else -> { viewModel.onClickAppBarDeleteButton() }
                    }
                },
            )
        }
    }
    private fun setRecyclerView() {
        archiveAdapter = ArchivePhotoAdapter(
            isSelectionMode = { viewModel.state.value.footerType == FooterType.DELETE },
            onImageClick = { spot ->
                if (viewModel.state.value.footerType == FooterType.DELETE) {
                    viewModel.togglePhotoSelection(spot)
                } else {
                    viewModel.onClickSpotImage(spot.spotId)
                }
            }
        )

        binding.rvArchive.apply {
            layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT)
            adapter = archiveAdapter
            addItemDecoration(GridSpaceItemDecoration(SPAN_COUNT, GRID_SPACE))
        }
    }
    private fun setButton() {
        binding.layoutExploreSpotButton.setOnClickListener {
            viewModel.onClickExploreSpotButton()
        }
    }

    private fun showDeleteImageDialog(count: Int) {
        PatataAlert(requireContext())
            .title(getString(R.string.title_dialog_archive_images_delete, count))
            .multiButton {
                leftButton(getString(R.string.cancel)) { }
                rightButton(getString(R.string.delete)) { viewModel.onClickDeleteButton() }
            }.show()
    }
    private fun showSnackBar(message: String) { SnackBarUtil.show(binding.root, message) }

    private fun navigateSpotDetail(spotId: Int) { (activity as GlobalNavigation).navigateSpotDetail(spotId) }
    private fun navigateCategorySpot(categoryId: Int) {
        (activity as GlobalNavigation).navigateCategorySpots(categoryId)
    }

    companion object {
        private const val SPAN_COUNT = 3
        private const val GRID_SPACE = 12 // 아이템 간격 (px)
    }
}