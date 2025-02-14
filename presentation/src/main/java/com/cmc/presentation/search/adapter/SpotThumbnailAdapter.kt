package com.cmc.presentation.search.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.cmc.common.constants.BundleKeys
import com.cmc.common.util.DistanceFormatter.formatDistance
import com.cmc.design.databinding.ViewSpotThumbnailBinding
import com.cmc.presentation.search.model.SpotWithDistanceUiModel

class SpotThumbnailAdapter(
    private val onArchiveClick: (Int) -> Unit,
    private val onImageClick: (Int) -> Unit,
) :
    PagingDataAdapter<SpotWithDistanceUiModel, SpotThumbnailAdapter.SpotViewHolder>(SpotDiffCallback()) {

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

    override fun onBindViewHolder(
        holder: SpotViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val item = getItem(position) ?: return

        if (payloads.isNotEmpty()) {
            val bundle = payloads[0] as Bundle
            if (bundle.containsKey(BundleKeys.Spot.KEY_IS_CHANGED_SCRAP)) {
                with(holder.binding) {
                    ivSpotArchive.isSelected = item.isScraped
                    item.scrapCount.toString().also { tvArchiveCount.text = it }
                }
            }
        } else {
            holder.bind(item, onArchiveClick, onImageClick)
        }

    }

    class SpotViewHolder(val binding: ViewSpotThumbnailBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
        fun bind (
            item: SpotWithDistanceUiModel,
            onArchiveClick: (Int) -> Unit,
            onImageClick: (Int) -> Unit
        ) {
            with(binding) {
                Glide.with(ivSpotImage.context)
                    .load(item.image)
                    .placeholder(com.cmc.design.R.drawable.img_sample)
                    .into(ivSpotImage)

                tvSpotTitle.text = item.spotName
                item.distance.also { tvSpotDistance.text = formatDistance(it) }
                item.scrapCount.toString().also { tvArchiveCount.text = it }
                item.reviewCount.toString().also { tvReviewCount.text = it }
                ivSpotArchive.isSelected = item.isScraped

                ivSpotArchive.setOnClickListener { onArchiveClick.invoke(item.spotId) }
                ivSpotImage.setOnClickListener { onImageClick.invoke(item.spotId) }
            }
        }
    }

    class SpotDiffCallback : DiffUtil.ItemCallback<SpotWithDistanceUiModel>() {
        override fun areItemsTheSame(oldItem: SpotWithDistanceUiModel, newItem: SpotWithDistanceUiModel): Boolean {
            return oldItem.spotId == newItem.spotId
        }

        override fun areContentsTheSame(oldItem: SpotWithDistanceUiModel, newItem: SpotWithDistanceUiModel): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(
            oldItem: SpotWithDistanceUiModel,
            newItem: SpotWithDistanceUiModel
        ): Any? {
            return if (oldItem.isScraped != newItem.isScraped) {
                Bundle().apply { putBoolean(BundleKeys.Spot.KEY_IS_CHANGED_SCRAP, true) }
            } else null
        }
    }
}
