package com.cmc.presentation.map

import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmc.common.base.BaseFragment
import com.cmc.design.component.BottomSheetDialog
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.databinding.ContentSheetAddSpotCategoryFilterBinding
import com.cmc.presentation.databinding.FragmentAddSpotBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.cmc.presentation.map.AddSpotViewModel.AddSpotSideEffect.*
import com.cmc.presentation.model.SpotCategoryItem

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
        setupRecyclerView()
        setupViewActionListeners()
    }

    private fun setupRecyclerView() {
        imageAdapter = SelectedImageAdapter { uri -> viewModel.removeSelectedImage(uri) }

        binding.rvSelectedImages.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }

        val itemTouchHelper = ItemTouchHelper(createItemTouchCallback())
        itemTouchHelper.attachToRecyclerView(binding.rvSelectedImages)
    }
    private fun setupViewActionListeners() {
        binding.etInputHashtag.setOnSubmitListener { str ->
            binding.etInputHashtag.clearEditor()
            viewModel.addTag(str)
        }

        binding.layoutChooseCategoryTitle.setOnClickListener { viewModel.openCategoryPicker() }
        binding.layoutChoosePictureButton.setOnClickListener { viewModel.openPhotoPicker() }
    }

    private fun updateUI(state: AddSpotViewModel.AddSpotState) {
        state.selectedCategory?.let {
            binding.tvChooseCategory.text = resources.getString(SpotCategoryItem(it).getName())
        }

        updateTags(state.tags)
        imageAdapter.updateImages(state.selectedImages)
        binding.layoutRegisterButton.isEnabled = state.isRegisterEnabled
    }
    private fun handleSideEffect(effect: AddSpotViewModel.AddSpotSideEffect) {
        when (effect) {
            is ShowCategoryPicker -> showCategoryFilter()
            is NavigateToSpotAddedSuccess -> navigate(R.id.navigate_spot_added_success)
            is ShowPhotoPicker -> pickImagesLauncher.launch("image/*")
            else -> {}
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
        if (uris.isNotEmpty()) { viewModel.updateSelectedImages(uris) }
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
                viewModel.updateSelectedImages(imageAdapter.getImages())
                imageAdapter.notifyDataSetChanged()
            }
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        override fun isLongPressDragEnabled(): Boolean = true
    }
}