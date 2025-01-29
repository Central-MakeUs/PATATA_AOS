package com.cmc.presentation.map

import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.cmc.common.base.BaseFragment
import com.cmc.design.component.BottomSheetDialog
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.databinding.ContentSheetAddSpotCategoryFilterBinding
import com.cmc.presentation.databinding.FragmentAddSpotBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.cmc.presentation.map.AddSpotViewModel.AddSpotSideEffect
import com.cmc.presentation.model.SpotCategoryItem

@AndroidEntryPoint
class AddSpotFragment: BaseFragment<FragmentAddSpotBinding>(R.layout.fragment_add_spot) {

    private val viewModel: AddSpotViewModel by viewModels()

    override fun initObserving() {
        repeatWhenUiStarted {
            launch {
                viewModel.state.collectLatest { state ->
                    when {
                        state.isLoading -> {}
                        state.errorMessage != null -> {}
                        else -> {
                            updateUI(state)
                        }
                    }
                }
            }

            launch {
                viewModel.sideEffect.collect { effect ->
                    when (effect) {
                        is AddSpotSideEffect.ShowCategoryPicker -> { showCategoryFilter() }
                        is AddSpotSideEffect.NavigateToSpotAddedSuccess -> { navigate(R.id.navigate_spot_added_success) }
                        else -> {}
                    }
                }
            }
        }
    }

    override fun initView() {
        binding.etInputHashtag.setOnSubmitListener { str ->
            binding.etInputHashtag.clearEditor()
            viewModel.addTag(str)
        }

        binding.layoutChooseCategoryTitle.setOnClickListener { viewModel.openCategoryPicker() }
    }

    private fun updateUI(state: AddSpotViewModel.AddSpotState) {
        state.selectedCategory?.let {
            binding.tvChooseCategory.text = resources.getString(SpotCategoryItem(it).getName())
        }

        updateTags(state.tags)

        binding.layoutRegisterButton.isEnabled = state.isRegisterEnabled
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
}