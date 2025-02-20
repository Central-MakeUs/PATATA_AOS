package com.cmc.presentation.archive.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cmc.presentation.R
import com.cmc.presentation.databinding.ViewArchivePhotoBinding
import com.cmc.presentation.spot.model.ScrapSpotUiModel

class ArchivePhotoAdapter(
    private val isSelectionMode: () -> Boolean,
    private val onImageClick: (ScrapSpotUiModel) -> Unit,
) : RecyclerView.Adapter<ArchivePhotoAdapter.ArchivePhotoViewHolder>() {

    private var items: List<ScrapSpotUiModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchivePhotoViewHolder {
        val binding = ViewArchivePhotoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ArchivePhotoViewHolder(binding, isSelectionMode, onImageClick)
    }

    override fun onBindViewHolder(holder: ArchivePhotoViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<ScrapSpotUiModel>) {
        val diffCallback = DiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    class ArchivePhotoViewHolder(
        private val binding: ViewArchivePhotoBinding,
        private val isSelectionMode: () -> Boolean,
        private val onImageClick: (ScrapSpotUiModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ScrapSpotUiModel) {
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

    // DiffUtil 콜백
    class DiffCallback(
        private val oldList: List<ScrapSpotUiModel>,
        private val newList: List<ScrapSpotUiModel>
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
