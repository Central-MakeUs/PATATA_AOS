package com.cmc.presentation.archive.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cmc.design.databinding.ViewSpotPolaroidShimmerBinding
import com.cmc.presentation.R
import com.cmc.presentation.databinding.ViewArchivePhotoBinding
import com.cmc.presentation.home.adapter.SpotPolaroidAdapter
import com.cmc.presentation.home.adapter.SpotPolaroidAdapter.Companion
import com.cmc.presentation.home.adapter.SpotPolaroidAdapter.SkeletonViewHolder
import com.cmc.presentation.spot.model.SpotPreviewUiModel

class ArchivePhotoAdapter(
    private val isSelectionMode: () -> Boolean,
    private val onImageClick: (SpotPreviewUiModel) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isLoading: Boolean = true

    companion object {
        private const val VIEW_TYPE_SKELETON = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    private var items: List<SpotPreviewUiModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SKELETON) {
            val view = ViewSpotPolaroidShimmerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SkeletonViewHolder(view.root.rootView)
        } else {
            val binding = ViewArchivePhotoBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            ArchivePhotoViewHolder(binding, isSelectionMode, onImageClick)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) VIEW_TYPE_SKELETON else VIEW_TYPE_ITEM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ArchivePhotoViewHolder) {
            holder.bind(items[position])
        }
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<SpotPreviewUiModel>, isLoading: Boolean) {
        this.isLoading = isLoading
        val diffCallback = DiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    class ArchivePhotoViewHolder(
        private val binding: ViewArchivePhotoBinding,
        private val isSelectionMode: () -> Boolean,
        private val onImageClick: (SpotPreviewUiModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SpotPreviewUiModel) {
            Glide.with(binding.root)
                .load(item.representativeImageUrl)
                .into(binding.ivPhoto)

            // UI 업데이트
            val selectionMode = isSelectionMode()
            val selected = item.isSelected

            binding.ivCheck.isVisible = selectionMode
            binding.ivCheck.setImageResource(
                if (selected) R.drawable.ic_circle_checked else R.drawable.ic_circle_unchecked
            )

            binding.root.setOnClickListener {
                onImageClick.invoke(item)
            }
        }
    }

    class SkeletonViewHolder(view: View) : RecyclerView.ViewHolder(view)

    // DiffUtil 콜백
    class DiffCallback(
        private val oldList: List<SpotPreviewUiModel>,
        private val newList: List<SpotPreviewUiModel>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].spotId == newList[newItemPosition].spotId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
