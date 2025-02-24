package com.cmc.presentation.home.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cmc.common.constants.BundleKeys
import com.cmc.design.component.SpotPolaroidView
import com.cmc.design.databinding.ViewSpotPolaroidShimmerBinding
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.map.model.TodayRecommendedSpotUiModel

class SpotPolaroidAdapter(
    initialItems: List<TodayRecommendedSpotUiModel>,
    private var isLoading: Boolean = true,
    private val onArchiveClick: (Int) -> Unit,
    private val onImageClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items: MutableList<TodayRecommendedSpotUiModel> = initialItems.toMutableList()

    companion object {
        private const val VIEW_TYPE_SKELETON = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SKELETON) {
            val view = ViewSpotPolaroidShimmerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SkeletonViewHolder(view.root.rootView)
        } else {
            val spotView = SpotPolaroidView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            SpotPolaroidViewHolder(spotView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) VIEW_TYPE_SKELETON else VIEW_TYPE_ITEM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SpotPolaroidViewHolder) {
            val actualPosition = position % items.size
            val spot = items[actualPosition]
            holder.bind(spot, onArchiveClick, onImageClick)
        }
    }

    fun setLoadingState(isLoading: Boolean) {
        this.isLoading = isLoading
        if (isLoading.not()) {
            notifyDataSetChanged()
        }
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

    class SkeletonViewHolder(view: View) : RecyclerView.ViewHolder(view)

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