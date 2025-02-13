package com.cmc.presentation.home.adapter

import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cmc.common.constants.BundleKeys
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.spot.component.SpotHorizontalCardView
import com.cmc.presentation.spot.model.SpotWithStatusUiModel

class SpotHorizontalCardAdapter(
    private val onArchiveClick: (Int) -> Unit,
    private val onImageClick: (Int) -> Unit
) : RecyclerView.Adapter<SpotHorizontalCardAdapter.SpotHorizontalCardViewHolder>() {

    private var items: List<SpotWithStatusUiModel> = emptyList()

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
        val spot = items[position]
        holder.bind(spot)
    }

    override fun onBindViewHolder(
        holder: SpotHorizontalCardViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val item = items[position] ?: return

        if (payloads.isNotEmpty()) {
            val bundle = payloads[0] as Bundle
            if (bundle.containsKey(BundleKeys.Spot.KEY_IS_CHANGED_SCRAP)) {
                holder.binding.updateScrapState(item.isScraped, item.scrapCount)
            }
        } else {
            holder.bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<SpotWithStatusUiModel>) {
        val diffCallback = DiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    class SpotHorizontalCardViewHolder(
        val binding: SpotHorizontalCardView,
        private val onArchiveClick: (Int) -> Unit,
        private val onImageClick: (Int) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding) {

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

    class DiffCallback(
        private val oldList: List<SpotWithStatusUiModel>,
        private val newList: List<SpotWithStatusUiModel>
    ): DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].spot.spotId == newList[newItemPosition].spot.spotId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return if (oldList[oldItemPosition].isScraped != newList[newItemPosition].isScraped) {
                Bundle().apply { putBoolean(BundleKeys.Spot.KEY_IS_CHANGED_SCRAP, true) }
            } else null
        }
    }
}