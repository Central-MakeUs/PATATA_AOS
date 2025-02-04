package com.cmc.presentation.archive

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.cmc.common.adapter.GridSpaceItemDecoration
import com.cmc.common.base.BaseFragment
import com.cmc.design.component.PatataAlert
import com.cmc.design.component.PatataAppBar
import com.cmc.presentation.R
import com.cmc.presentation.databinding.FragmentArchiveBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.cmc.presentation.archive.ArchiveViewModel.ArchiveState
import com.cmc.presentation.archive.ArchiveViewModel.ArchiveSideEffect
import com.cmc.design.component.PatataAppBar.FooterType

@AndroidEntryPoint
class ArchiveFragment: BaseFragment<FragmentArchiveBinding>(R.layout.fragment_archive) {

    private val viewModel: ArchiveViewModel by viewModels()

    private lateinit var archiveAdapter: ArchivePhotoAdapter

    override fun initObserving() {
        repeatWhenUiStarted {
            launch {
                viewModel.state.collectLatest { state ->
                    updateUI(state)
                }
            }
            launch {
                viewModel.sideEffect.collect { effect ->
                    handleSideEffect(effect)
                }
            }
        }
    }
    override fun initView() {
        setAppBar()
        setRecyclerView()
    }

    private fun updateUI(state: ArchiveState) {
        binding.layoutArchiveNoResult.isVisible = state.images.isEmpty()

        binding.archiveAppbar.setFooterType(state.footerType)
        // Adapter에 데이터 변경을 알림 (선택 모드가 바뀌었으므로 모든 아이템 갱신)
        archiveAdapter.setItems(state.images)
        archiveAdapter.notifyDataSetChanged()
    }
    private fun handleSideEffect(effect: ArchiveSideEffect) {
        when (effect) {
            is ArchiveSideEffect.ShowDeleteImageDialog -> { showDeleteImageDialog(effect.selectedImages) }
            is ArchiveSideEffect.ShowSnackbar -> {}
        }
    }

    private fun setAppBar() {
        binding.archiveAppbar.apply {
            setupAppBar(
                title = getString(R.string.title_archive),
                onFootButtonClick = { type ->
                    when (type) {
                        PatataAppBar.FooterType.SELECT -> { viewModel.onClickSelectButton() }
                        else -> { viewModel.onClickDeleteButton() }
                    }
                },
            )
        }
    }
    private fun setRecyclerView() {
        viewModel.getDumpData()

        archiveAdapter = ArchivePhotoAdapter(
            isSelectionMode = { viewModel.state.value.footerType == FooterType.DELETE },
            isSelected = { imageId -> viewModel.state.value.selectedImages.contains(imageId) },
            onPhotoClick = { imageId ->
                if (viewModel.state.value.footerType == FooterType.DELETE) {
                    viewModel.togglePhotoSelection(imageId)
                } else {
                    // 상세 화면 이동
                }
            }
        )

        binding.rvArchive.apply {
            layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT) // 2열 그리드
            adapter = archiveAdapter
            addItemDecoration(GridSpaceItemDecoration(SPAN_COUNT, GRID_SPACE))
        }
    }

    private fun showDeleteImageDialog(images: List<Int>) {
        PatataAlert(requireContext())
            .title(getString(R.string.title_dialog_archive_images_delete, images.size))
            .multiButton {
                leftButton(getString(R.string.cancel)) { }
                rightButton(getString(R.string.delete)) {
                    // TODO: 이미지 삭제 API
                    viewModel.tempDeleteImages(images)
                }
            }.show()
    }

    private fun getDumpData(): List<Pair<Int, String>> {
        return List(41) { it to "https://source.unsplash.com/random/400x400?nature${(it % 6) + 1}" }
    }

    companion object {
        private const val SPAN_COUNT = 2
        private const val GRID_SPACE = 12 // 아이템 간격 (px)
    }
}