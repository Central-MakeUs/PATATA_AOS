package com.cmc.presentation.home.adapter

import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cmc.common.constants.BundleKeys
import com.cmc.design.component.SpotPolaroidView
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.map.model.TodayRecommendedSpotUiModel

class SpotPolaroidAdapter(
    private var items: List<TodayRecommendedSpotUiModel>,
    private val onArchiveClick: (Int) -> Unit,
    private val onImageClick: (Int) -> Unit
) : RecyclerView.Adapter<SpotPolaroidAdapter.SpotPolaroidViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotPolaroidViewHolder {
        val spotView = SpotPolaroidView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        return SpotPolaroidViewHolder(spotView)
    }

    override fun onBindViewHolder(holder: SpotPolaroidViewHolder, position: Int) {
        val actualPosition = position % items.size
        val spot = items[actualPosition]
        holder.bind(spot, onArchiveClick, onImageClick)
    }

    fun setItems(newItems: List<TodayRecommendedSpotUiModel>) {
        if (items == newItems) {
            notifyDataSetChanged() // 강제 UI 갱신
            return
        }
        val diffCallback = DiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    class SpotPolaroidViewHolder(private val spotView: SpotPolaroidView) :
        RecyclerView.ViewHolder(spotView) {

        fun bind(
            spot: TodayRecommendedSpotUiModel,
            onArchiveClick: (Int) -> Unit,
            onImageClick: (Int) -> Unit
        ) {
            spotView.setSpotPolaroidView(
                title = spot.spotName,
                location = spot.address,
                imageUrl = spot.images[0],
                tags = spot.tags,
                isArchived = spot.isScraped,
                isBadgeVisible = SpotCategory.isRecommended(spot.categoryId),
                archiveClick = { onArchiveClick.invoke(spot.spotId) },
                imageClick = { onImageClick.invoke(spot.spotId) },
            )
        }
    }


    class DiffCallback(
        private val oldList: List<TodayRecommendedSpotUiModel>,
        private val newList: List<TodayRecommendedSpotUiModel>
    ): DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].spotId == newList[newItemPosition].spotId
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