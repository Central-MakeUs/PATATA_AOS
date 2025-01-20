package com.cmc.presentation.home

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmc.design.component.SpotHorizontalCardView
import com.cmc.design.component.SpotHorizontalCardView.SpotHorizontalCardItem

class CategoryRecommendAdapter(
    private val spotList: List<SpotHorizontalCardItem>,
    private val onArchiveClick: (SpotHorizontalCardItem) -> Unit,
    private val onImageClick: (SpotHorizontalCardItem) -> Unit
) : RecyclerView.Adapter<CategoryRecommendAdapter.CategoryRecommendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryRecommendViewHolder {
        val spotView = SpotHorizontalCardView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        return CategoryRecommendViewHolder(spotView)
    }

    override fun onBindViewHolder(holder: CategoryRecommendViewHolder, position: Int) {
        val spot = spotList[position]
        holder.bind(spot)
    }

    override fun getItemCount(): Int = spotList.size

    inner class CategoryRecommendViewHolder(private val cardView: SpotHorizontalCardView) :
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