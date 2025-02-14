package com.cmc.presentation.home.adapter

import android.os.Bundle
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.cmc.common.constants.BundleKeys
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.spot.component.SpotHorizontalCardView
import com.cmc.presentation.spot.model.SpotWithStatusUiModel

class SpotHorizontalPaginatedCardAdapter(
    private val onArchiveClick: (Int) -> Unit,
    private val onImageClick: (Int) -> Unit
) : PagingDataAdapter<SpotWithStatusUiModel, SpotHorizontalPaginatedCardAdapter.SpotHorizontalCardViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotHorizontalCardViewHolder {
        val spotView = SpotHorizontalCardView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        return SpotHorizontalCardViewHolder(spotView, onArchiveClick, onImageClick)
    }

    override fun onBindViewHolder(holder: SpotHorizontalCardViewHolder, position: Int) {
        val spot = getItem(position) ?: return
        holder.bind(spot)
    }

    override fun onBindViewHolder(
        holder: SpotHorizontalCardViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val item = getItem(position) ?: return

        if (payloads.isNotEmpty()) {
            val bundle = payloads[0] as Bundle
            if (bundle.containsKey(BundleKeys.Spot.KEY_IS_CHANGED_SCRAP)) {
                holder.binding.updateScrapState(item.isScraped, item.scrapCount)
            }
        } else {
            holder.bind(item)
        }
    }

    class SpotHorizontalCardViewHolder(
        val binding: SpotHorizontalCardView,
        private val onArchiveClick: (Int) -> Unit,
        private val onImageClick: (Int) -> Unit,
    ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding) {

        fun bind(spot: SpotWithStatusUiModel) {
            binding.setHorizontalCardView(
                imageUrl = spot.image,
                title = spot.spot.spotName,
                location = spot.spot.address,
                archiveCount = spot.scrapCount,
                commentCount = spot.reviewCount,
                tags = spot.spot.tags,
                isScraped = spot.isScraped,
                isRecommended = SpotCategory.isRecommended(spot.spot.categoryId),
                archiveClickListener = { onArchiveClick.invoke(spot.spot.spotId) },
                cardClickListener = { onImageClick.invoke(spot.spot.spotId) }
            )
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SpotWithStatusUiModel>() {
            override fun areItemsTheSame(oldItem: SpotWithStatusUiModel, newItem: SpotWithStatusUiModel): Boolean {
                return oldItem.spot.spotId == newItem.spot.spotId
            }

            override fun areContentsTheSame(oldItem: SpotWithStatusUiModel, newItem: SpotWithStatusUiModel): Boolean {
                return oldItem == newItem
            }

            override fun getChangePayload(
                oldItem: SpotWithStatusUiModel,
                newItem: SpotWithStatusUiModel
            ): Any? {
                return if (oldItem.isScraped != newItem.isScraped) {
                    Bundle().apply { putBoolean(BundleKeys.Spot.KEY_IS_CHANGED_SCRAP, true) }
                } else null
            }
        }
    }
}
