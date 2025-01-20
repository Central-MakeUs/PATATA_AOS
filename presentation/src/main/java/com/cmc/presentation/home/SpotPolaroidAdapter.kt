package com.cmc.presentation.home

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmc.design.component.SpotPolaroidView
import com.cmc.design.component.SpotPolaroidView.SpotPolaroidItem

class SpotPolaroidAdapter(
    private val spotList: List<SpotPolaroidItem>,
    private val onArchiveClick: (SpotPolaroidItem) -> Unit,
    private val onImageClick: (SpotPolaroidItem) -> Unit
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
        val actualPosition = position % spotList.size
        val spot = spotList[actualPosition]
        holder.bind(spot)
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    inner class SpotPolaroidViewHolder(private val spotView: SpotPolaroidView) :
        RecyclerView.ViewHolder(spotView) {

        fun bind(spot: SpotPolaroidItem) {
            spotView.setSpotPolaroidView(
                title = spot.title,
                location = spot.location,
                imageResId = spot.imageResId,
                tags = spot.tags,
                isArchived = spot.isArchived,
                isBadgeVisible = spot.isRecommended,
                archiveClick = { onArchiveClick.invoke(spot) },
                imageClick = { onImageClick.invoke(spot) },
            )
        }
    }
}