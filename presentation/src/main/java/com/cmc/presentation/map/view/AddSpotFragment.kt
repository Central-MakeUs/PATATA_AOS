package com.cmc.presentation.map.view

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmc.common.base.BaseFragment
import com.cmc.common.constants.NavigationKeys
import com.cmc.design.component.BottomSheetDialog
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.databinding.ContentSheetAddSpotCategoryFilterBinding
import com.cmc.presentation.databinding.FragmentAddSpotBinding
import com.cmc.presentation.map.adapter.SelectedImageAdapter
import com.cmc.presentation.map.viewmodel.AddSpotViewModel
import com.cmc.presentation.map.viewmodel.AddSpotViewModel.AddSpotSideEffect.NavigateToAroundMe
import com.cmc.presentation.map.viewmodel.AddSpotViewModel.AddSpotSideEffect.ShowCategoryPicker
import com.cmc.presentation.map.viewmodel.AddSpotViewModel.AddSpotSideEffect.ShowPhotoPicker
import com.cmc.presentation.map.viewmodel.AddSpotViewModel.AddSpotSideEffect.NavigateToCreateSpotSuccess
import com.cmc.presentation.map.viewmodel.AddSpotViewModel.AddSpotSideEffect.ShowSnackbar
import com.cmc.presentation.model.SpotCategoryItem
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddSpotFragment: BaseFragment<FragmentAddSpotBinding>(R.layout.fragment_add_spot) {

    private val viewModel: AddSpotViewModel by viewModels()

    private lateinit var imageAdapter: SelectedImageAdapter

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
        setArgument(arguments)
        setAppbar()
        setupRecyclerView()
        setupViewActionListeners()
    }

    private fun setArgument(bundle: Bundle?) {
        val latitude = bundle?.getDouble(NavigationKeys.AddSpot.ARGUMENT_LATITUDE)
        val longitude = bundle?.getDouble(NavigationKeys.AddSpot.ARGUMENT_LONGITUDE)
        val address = bundle?.getString(NavigationKeys.AddSpot.ARGUMENT_ADDRESS_NAME)

        if (latitude == null || longitude == null || address.isNullOrEmpty()) {
            // 필수 인자가 없을 때의 처리 (예: 에러 메시지 표시)
            return
        }

        viewModel.updateLocationWithAddress(latitude, longitude, address)
    }

    private fun setAppbar() {
        binding.addSpotAppbar.apply {
            setupAppBar(
                title = getString(R.string.title_add_a_spot),
                onHeadButtonClick = { finish() },
                onFootButtonClick = { viewModel.onCancelButtonClicked()  }
            )
        }
    }
    private fun setupRecyclerView() {
        imageAdapter = SelectedImageAdapter { image -> viewModel.removeSelectedImage(image) }

        binding.rvSelectedImages.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }

        val itemTouchHelper = ItemTouchHelper(createItemTouchCallback())
        itemTouchHelper.attachToRecyclerView(binding.rvSelectedImages)
    }
    private fun setupViewActionListeners() {
        binding.etInputTitle.setAfterTextChangeListener { str ->
            viewModel.updateSpotName(str)
        }

        binding.etInputAddressDetail.setAfterTextChangeListener { str ->
            viewModel.updateAddressDetail(str)
        }

        binding.etContentDesc.setAfterTextChangeListener { str ->
            viewModel.updateDescription(str)
        }

        binding.etInputHashtag.setOnSubmitListener { str ->
            binding.etInputHashtag.clearEditor()
            viewModel.addTag(str)
        }

        binding.layoutChooseCategoryTitle.setOnClickListener { viewModel.openCategoryPicker() }
        binding.layoutChoosePictureButton.setOnClickListener { viewModel.openPhotoPicker() }
        binding.layoutRegisterButton.setOnClickListener { viewModel.onClickRegisterButton() }
    }

    private fun updateUI(state: AddSpotViewModel.AddSpotState) {
        binding.apply {
            tvInputAddress.text = state.address
            groupSpotDesc.isVisible = state.description.isEmpty()

            // 카테고리 선택 UI 업데이트
            tvChooseCategory.apply {
                text = state.selectedCategory?.let { getString(SpotCategoryItem(it).getName()) } ?: getString(R.string.hint_choose_category)
                setTextAppearance(
                    if (state.selectedCategory != null) com.cmc.design.R.style.subtitle_small
                    else com.cmc.design.R.style.body_small
                )
                setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (state.selectedCategory != null) com.cmc.design.R.color.text_sub
                        else com.cmc.design.R.color.text_disabled
                    )
                )
            }

            // 태그 및 이미지 업데이트
            updateTags(state.tags)
            imageAdapter.updateImages(state.selectedImages.toList())

            // 등록 버튼 활성화 상태 업데이트
            layoutRegisterButton.isEnabled = state.isRegisterEnabled
        }
    }
    private fun handleSideEffect(effect: AddSpotViewModel.AddSpotSideEffect) {
        when (effect) {
            is ShowCategoryPicker -> showCategoryFilter()
            is ShowPhotoPicker -> pickImagesLauncher.launch("image/*")
            is NavigateToAroundMe -> { navigateAroundMe() }
            is NavigateToCreateSpotSuccess -> { navigateCreateSpotSuccess() }
            is ShowSnackbar -> { showSnackBar(effect.message) }
        }
    }

    private fun updateTags(tags: List<String>) {
        binding.layoutHashtagContainer.removeAllViews()
        tags.forEach { tagText ->
            val tagView = LayoutInflater.from(requireContext()).inflate(R.layout.view_cancelable_tag, binding.layoutHashtagContainer, false) as TextView
            tagView.apply {
                text = tagText
                setCompoundDrawablesWithIntrinsicBounds(0, 0, com.cmc.design.R.drawable.ic_cancel_mini, 0)
                compoundDrawablePadding = resources.getDimensionPixelSize(com.cmc.design.R.dimen.hashtag_drawable_padding)
                setOnClickListener {
                    viewModel.removeTag(tagText)
                }
            }
            binding.layoutHashtagContainer.addView(tagView)
        }
    }
    private fun showCategoryFilter() {
        val dialogContentBinding = ContentSheetAddSpotCategoryFilterBinding.inflate(LayoutInflater.from(requireContext()))
        BottomSheetDialog(requireContext())
            .bindBuilder(
                dialogContentBinding
            ) { dialog ->

                updateDialogUI(dialogContentBinding, viewModel.state.value.selectedCategory) {
                    dialog.dismiss()
                }

                dialog.show()
            }
    }
    private fun updateDialogUI(
        binding: ContentSheetAddSpotCategoryFilterBinding,
        selectedCategory: SpotCategory?,
        dismissListener: () -> Unit) {
        val categoryViews = mapOf(
            SpotCategory.SNAP to binding.tvCategorySnapSpot,
            SpotCategory.NIGHT to binding.tvCategoryNightView,
            SpotCategory.EVERYDAY to binding.tvCategoryEverydayLife,
            SpotCategory.NATURE to binding.tvCategoryNature
        )

        fun getColor(isSelected: Boolean) =
            ContextCompat.getColor(binding.root.context, if (isSelected) com.cmc.design.R.color.black else com.cmc.design.R.color.text_disabled)

        categoryViews.forEach { (category, textView) ->
            textView.setTextColor(getColor(category == selectedCategory))
            textView.setOnClickListener {
                viewModel.selectCategory(category)
                dismissListener.invoke()
            }
        }
    }

    private val pickImagesLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.updateSelectedImages(uris)
        }
    }

    private fun createItemTouchCallback() = object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            return makeMovementFlags(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0)
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            val fromPosition = viewHolder.absoluteAdapterPosition
            val toPosition = target.absoluteAdapterPosition
            imageAdapter.moveItem(fromPosition, toPosition)
            return true
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                viewModel.setSelectedImages(imageAdapter.getImages())
            }
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        override fun isLongPressDragEnabled(): Boolean = true
    }

    private fun showSnackBar(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).apply {
            setBackgroundTint(ContextCompat.getColor(requireContext(), com.cmc.design.R.color.gray_100))
            setTextColor(ContextCompat.getColor(requireContext(), com.cmc.design.R.color.blue_20))
        }

        // 텍스트 중앙 정렬
        val textView = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        textView.gravity = Gravity.CENTER

        snackbar.show()
    }
    private fun navigateAroundMe(){ navigate(R.id.navigate_add_spot_to_around_me) }
    private fun navigateCreateSpotSuccess() { navigate(R.id.navigate_spot_added_success) }
}