package com.cmc.presentation.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cmc.presentation.spot.component.SpotHorizontalCardView
import com.cmc.presentation.spot.model.SpotWithStatusUiModel

class SpotHorizontalCardAdapter(
    private val onArchiveClick: (SpotWithStatusUiModel) -> Unit,
    private val onImageClick: (SpotWithStatusUiModel) -> Unit
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
    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<SpotWithStatusUiModel>) {
        val diffCallback = DiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    class SpotHorizontalCardViewHolder(
        private val cardView: SpotHorizontalCardView,
        private val onArchiveClick: (SpotWithStatusUiModel) -> Unit,
        private val onImageClick: (SpotWithStatusUiModel) -> Unit,
    ) :
        RecyclerView.ViewHolder(cardView) {

        fun bind(spot: SpotWithStatusUiModel) {
            cardView.setHorizontalCardView(
                imageUrl = spot.image, // Glide 등을 활용해 image를 로드할 수 있음
                title = spot.spot.spotName, // SpotUiModel에서 SpotName 가져오기
                location = spot.spot.address,
                archiveCount = spot.scrapCount,
                commentCount = spot.reviewCount,
                tags = spot.spot.tags,
                isRecommended = false, // 추천 여부 (추가 로직 필요)
                archiveClickListener = { onArchiveClick.invoke(spot) },
                cardClickListener = { onImageClick.invoke(spot) }
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
    }
}