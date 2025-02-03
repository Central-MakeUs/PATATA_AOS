package com.cmc.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.cmc.design.databinding.ViewSpotThumbnailBinding
import com.cmc.presentation.search.SearchViewModel.TempSpotResult

class SpotThumbnailAdapter(
    private val onArchiveClick: (TempSpotResult) -> Unit,
    private val onImageClick: (TempSpotResult) -> Unit,
) :
    PagingDataAdapter<TempSpotResult, SpotThumbnailAdapter.SpotViewHolder>(SpotDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotViewHolder {
        val binding = ViewSpotThumbnailBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SpotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpotViewHolder, position: Int) {
        val spot = getItem(position)
        spot?.let { holder.bind(it, onArchiveClick, onImageClick) }
    }

    class SpotViewHolder(private val binding: ViewSpotThumbnailBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        fun bind (
            item: TempSpotResult,
            onArchiveClick: (TempSpotResult) -> Unit,
            onImageClick: (TempSpotResult) -> Unit
        ) {
            Glide.with(binding.ivSpotImage.context)
                .load(item.imageUrl)
                .placeholder(com.cmc.design.R.drawable.img_sample)
                .into(binding.ivSpotImage)

            binding.tvSpotTitle.text = item.title
            "%.1f km".format(item.distance).also { binding.tvSpotDistance.text = it }
            item.likes.toString().also { binding.tvArchiveCount.text = it }
            item.scraps.toString().also { binding.tvReviewCount.text = it }
            binding.ivSpotArchive.isSelected = item.isBookmarked

            binding.ivSpotArchive.setOnClickListener {
                binding.ivSpotArchive.isSelected = binding.ivSpotArchive.isSelected.not()
                onArchiveClick.invoke(item)
            }

            binding.ivSpotImage.setOnClickListener {
                onImageClick.invoke(item)
            }
        }
    }

    class SpotDiffCallback : DiffUtil.ItemCallback<TempSpotResult>() {
        override fun areItemsTheSame(oldItem: TempSpotResult, newItem: TempSpotResult): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: TempSpotResult, newItem: TempSpotResult): Boolean {
            return oldItem == newItem
        }
    }
}
