package com.cmc.presentation.spot.view

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.common.constants.NavigationKeys
import com.cmc.design.component.BottomSheetDialog
import com.cmc.design.util.SnackBarUtil
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.databinding.ContentSheetAddSpotCategoryFilterBinding
import com.cmc.presentation.databinding.FragmentEditSpotBinding
import com.cmc.presentation.map.adapter.SelectedImageAdapter
import com.cmc.presentation.spot.viewmodel.EditSpotViewModel
import com.cmc.presentation.spot.viewmodel.EditSpotViewModel.EditSpotSideEffect.ShowCategoryPicker
import com.cmc.presentation.spot.viewmodel.EditSpotViewModel.EditSpotSideEffect.ShowPhotoPicker
import com.cmc.presentation.spot.viewmodel.EditSpotViewModel.EditSpotSideEffect.ShowSnackbar
import com.cmc.presentation.spot.viewmodel.EditSpotViewModel.EditSpotSideEffect.Finish
import com.cmc.presentation.spot.viewmodel.EditSpotViewModel.EditSpotSideEffect.NavigateSelectLocation
import com.cmc.presentation.model.SpotCategoryItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditSpotFragment: BaseFragment<FragmentEditSpotBinding>(R.layout.fragment_edit_spot) {

    private val viewModel: EditSpotViewModel by viewModels()

    private lateinit var imageAdapter: SelectedImageAdapter

    override fun initObserving() {
        repeatWhenUiStarted {
            launch { viewModel.state.collect { state -> updateUI(state) } }
            launch { viewModel.sideEffect.collectLatest { effect -> handleSideEffect(effect) } }
        }
    }

    override fun initView() {
        setSavedState()
        setArgument(arguments)
        setAppbar()
        setButton()
        setupRecyclerView()
        setupViewActionListeners()
    }

    private fun updateUI(state: EditSpotViewModel.EditSpotState) {
        binding.apply {
            tvChooseCategory.apply {
                text = state.selectedCategory?.let { getString(SpotCategoryItem(it).getName()) } ?: getString(
                    R.string.hint_choose_category)
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
            layoutEditButton.isEnabled = state.isRegisterEnabled
        }
    }
    private fun handleSideEffect(effect: EditSpotViewModel.EditSpotSideEffect) {
        when (effect) {
            is Finish -> { finish() }
            is ShowCategoryPicker -> showCategoryFilter()
            is ShowPhotoPicker -> pickImagesLauncher.launch("image/*")
            is NavigateSelectLocation -> { navigateSelectLocation(effect.latitude, effect.longitude)}
            is ShowSnackbar -> { showSnackBar(effect.message) }
        }
    }

    private fun setSavedState() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Bundle>("resultKey")
            ?.observe(viewLifecycleOwner) { bundle ->
                val addressName = bundle.getString("addressName") ?: viewModel.state.value.address
                val latitude = bundle.getDouble("latitude")
                val longitude = bundle.getDouble("longitude")
                viewModel.setSelectLocationResult(addressName, latitude, longitude)
                binding.etInputAddressDetail.setEditorText("")

                setDataBind(viewModel.state.value)
            }
    }
    private fun setArgument(bundle: Bundle?) {
        if (viewModel.state.value.isInitialized) return
        bundle?.getInt(NavigationKeys.SpotDetail.ARGUMENT_SPOT_ID)?.let {  spotId ->
            viewModel.fetchSpotDetail(spotId) { currentState ->
                setDataBind(currentState)
            }
        }
    }
    private fun setDataBind(currentState: EditSpotViewModel.EditSpotState) {
        with(binding) {
            etInputTitle.setEditorText(currentState.spotName)
            tvInputAddress.text = currentState.address
            currentState.addressDetail?.let { etInputAddressDetail.setEditorText(it) }
            etContentDesc.setEditorText(currentState.description)
            groupSpotDesc.isVisible = currentState.description.isEmpty()
        }
    }
    private fun setAppbar() {
        binding.addSpotAppbar.apply {
            setupAppBar(
                title = getString(R.string.title_edit_a_spot),
                onHeadButtonClick = { finish() },
                onFootButtonClick = { viewModel.onCancelButtonClicked()  }
            )
        }
    }
    private fun setButton() {
        binding.tvInputAddress.setOnClickListener { viewModel.onClickAddress() }
    }
    private fun setupRecyclerView() {
        imageAdapter = SelectedImageAdapter(isEditable = false) { image -> viewModel.removeSelectedImage(image) }

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
        binding.layoutEditButton.setOnClickListener { viewModel.onClickEditButton() }
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
    private fun showSnackBar(message: String) { SnackBarUtil.show(binding.root, message) }

    private fun navigateSelectLocation(latitude: Double, longitude: Double){
        (activity as GlobalNavigation).navigateSelectLocation(latitude, longitude, true)
    }
}