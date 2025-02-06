package com.cmc.presentation.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmc.design.component.SpotHorizontalCardView
import com.cmc.design.component.SpotHorizontalCardView.SpotHorizontalCardItem

class SpotHorizontalCardAdapter(
    private val spotList: List<SpotHorizontalCardItem>,
    private val onArchiveClick: (SpotHorizontalCardItem) -> Unit,
    private val onImageClick: (SpotHorizontalCardItem) -> Unit
) : RecyclerView.Adapter<SpotHorizontalCardAdapter.SpotHorizontalCardViewHolder>() {

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
        val spot = spotList[position]
        holder.bind(spot)
    }

    override fun getItemCount(): Int = spotList.size

    inner class SpotHorizontalCardViewHolder(private val cardView: SpotHorizontalCardView) :
        RecyclerView.ViewHolder(cardView) {

        fun bind(item: SpotHorizontalCardItem) {
            cardView.setHorizontalCardView(
                imageResId = item.imageResId,
                title = item.title,
                location = item.location,
                archiveCount = item.archiveCount,
                commentCount = item.commentCount,
                tags = item.tags,
                isRecommended = item.isRecommended,
                archiveClickListener = { onArchiveClick.invoke(item) },
                cardClickListener = { onImageClick.invoke(item) },
            )
        }
    }
}