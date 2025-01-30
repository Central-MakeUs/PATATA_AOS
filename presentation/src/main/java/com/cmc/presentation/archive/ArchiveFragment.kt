package com.cmc.presentation.archive

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
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
        binding.archiveAppbar.setFooterType(
            if (state.selectionMode == SelectionMode.DEFAULT) PatataAppBar.FooterType.SELECT
            else PatataAppBar.FooterType.DELETE
        )
        // Adapter에 데이터 변경을 알림 (선택 모드가 바뀌었으므로 모든 아이템 갱신)
        archiveAdapter.notifyDataSetChanged()
    }
    private fun handleSideEffect(effect: ArchiveSideEffect) {
        when (effect) {
            is ArchiveSideEffect.ShowDeleteImageDialog -> { showDeleteImageDialog(effect.images) }
            is ArchiveSideEffect.ShowSnackbar -> {}
        }
    }

    private fun setAppBar() {
        binding.archiveAppbar.apply {
            setupAppBar(
                title = getString(R.string.title_archive),
                onFootButtonClick = { type ->
                    when (type) {
                        PatataAppBar.FooterType.SELECT -> { viewModel.setSelectionMode() }
                        else -> { viewModel.onClickDeleteButton() }
                    }
                },
            )
        }
    }
    private fun setRecyclerView() {
        val sampleData = getDumpData()

        archiveAdapter = ArchivePhotoAdapter(
            items = sampleData,
            isSelectionMode = { viewModel.state.value.selectionMode == SelectionMode.SELECT },
            isSelected = { imageId -> viewModel.state.value.selectedItems.contains(imageId) },
            onPhotoClick = { imageId ->
                if (viewModel.state.value.selectionMode == SelectionMode.SELECT) {
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