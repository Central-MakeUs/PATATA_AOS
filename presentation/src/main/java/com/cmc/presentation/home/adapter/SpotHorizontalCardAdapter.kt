package com.cmc.presentation.home.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.cmc.presentation.spot.component.SpotHorizontalCardView
import com.cmc.presentation.spot.model.SpotWithStatusUiModel

class SpotHorizontalCardAdapter(
    private val onArchiveClick: (SpotWithStatusUiModel) -> Unit,
    private val onImageClick: (SpotWithStatusUiModel) -> Unit
) : PagingDataAdapter<SpotWithStatusUiModel, SpotHorizontalCardAdapter.SpotHorizontalCardViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotHorizontalCardViewHolder {
        val spotView = SpotHorizontalCardView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        return SpotHorizontalCardViewHolder(spotView)
    }

    override fun onBindViewHolder(holder: SpotHorizontalCardViewHolder, position: Int) {
        val spot = getItem(position) ?: return
        holder.bind(spot)
    }

    inner class SpotHorizontalCardViewHolder(private val cardView: SpotHorizontalCardView) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(cardView) {

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

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SpotWithStatusUiModel>() {
            override fun areItemsTheSame(oldItem: SpotWithStatusUiModel, newItem: SpotWithStatusUiModel): Boolean {
                return oldItem.spot.spotId == newItem.spot.spotId
            }

            override fun areContentsTheSame(oldItem: SpotWithStatusUiModel, newItem: SpotWithStatusUiModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}
