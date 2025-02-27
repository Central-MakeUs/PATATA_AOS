package com.cmc.presentation.map.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cmc.common.adapter.HorizontalSpaceItemDecoration
import com.cmc.common.constants.BundleKeys
import com.cmc.common.util.DistanceFormatter
import com.cmc.design.util.Util.dp
import com.cmc.design.util.animateClickEffect
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.databinding.ViewSpotDoubleImageBinding
import com.cmc.presentation.databinding.ViewSpotScrollImageBinding
import com.cmc.presentation.databinding.ViewSpotSingleImageBinding
import com.cmc.presentation.home.adapter.HorizontalImageAdapter
import com.cmc.presentation.map.model.SpotWithMapUiModel
import com.cmc.presentation.model.SpotCategoryItem

class MapSpotHorizontalMultiImageCardAdapter(
    private val onArchiveClick: (Int) -> Unit,
    private val onImageClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<SpotWithMapUiModel> = emptyList()

    override fun getItemViewType(position: Int): Int {
        return when (items[position].images.size) {
            1 -> VIEW_TYPE_SINGLE
            2 -> VIEW_TYPE_DOUBLE
            else -> VIEW_TYPE_SCROLL // `THRESHOLD_SCROLL` 기준으로 변경 가능
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SINGLE -> {
                val binding = ViewSpotSingleImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SingleImageViewHolder(binding)
            }
            VIEW_TYPE_DOUBLE -> {
                val binding = ViewSpotDoubleImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                DoubleImageViewHolder(binding)
            }
            else -> {
                val binding = ViewSpotScrollImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ScrollImageViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SingleImageViewHolder -> holder.bind(items[position], onArchiveClick, onImageClick)
            is DoubleImageViewHolder -> holder.bind(items[position], onArchiveClick, onImageClick)
            is ScrollImageViewHolder -> holder.bind(items[position], onArchiveClick, onImageClick)
        }
    }

    fun setItems(newItems: List<SpotWithMapUiModel>) {
        if (items == newItems) {
            notifyDataSetChanged() // 강제 UI 갱신
            return
        }
        val diffCallback = DiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = items.size


    class SingleImageViewHolder(private val binding: ViewSpotSingleImageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            spot: SpotWithMapUiModel,
            onArchiveClick: (Int) -> Unit,
            onImageClick: (Int) -> Unit
        ) {
            with(binding) {
                val context = root.context

                val category = SpotCategoryItem(SpotCategory.fromId(spot.categoryId))

                tvRecommendLabel.isVisible = SpotCategory.isRecommended(spot.categoryId)
                tvSpotTitle.text = spot.spotName
                tvCategory.text = context.getString(category.getName())
                category.getIcon()?.let { ivCategory.setImageResource(it) }
                ivSpotArchive.isSelected = spot.isScraped
                tvDistance.text = DistanceFormatter.formatDistance(spot.distance)
                "${spot.address} ${spot.addressDetail}".also { tvSpotLocation.text = it }

                layoutTagContainer.removeAllViews()
                spot.tags.forEach { tag ->
                    val tagView = LayoutInflater.from(context).inflate(com.cmc.design.R.layout.view_tag_blue, layoutTagContainer, false)
                    "#$tag".also { tagView.findViewById<TextView>(com.cmc.design.R.id.tv_tag).text = it }
                    layoutTagContainer.addView(tagView)
                }

                Glide.with(this.root)
                    .load(spot.images.first())
                    .thumbnail(0.25f)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(ivSpotImage)

                ivSpotArchive.setOnClickListener {
                    ivSpotArchive.animateClickEffect()
                    onArchiveClick.invoke(spot.spotId)
                }
                ivSpotImage.setOnClickListener { onImageClick.invoke(spot.spotId) }
            }
        }
    }

    class DoubleImageViewHolder(private val binding: ViewSpotDoubleImageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            spot: SpotWithMapUiModel,
            onArchiveClick: (Int) -> Unit,
            onImageClick: (Int) -> Unit
        ) {
            with(binding) {
                val context = root.context

                val category = SpotCategoryItem(SpotCategory.fromId(spot.categoryId))

                tvRecommendLabel.isVisible = SpotCategory.isRecommended(spot.categoryId)
                tvSpotTitle.text = spot.spotName
                tvCategory.text = context.getString(category.getName())
                category.getIcon()?.let { ivCategory.setImageResource(it) }
                ivSpotArchive.isSelected = spot.isScraped
                tvDistance.text = DistanceFormatter.formatDistance(spot.distance)
                "${spot.address} ${spot.addressDetail}".also { tvSpotLocation.text = it }

                layoutTagContainer.removeAllViews()
                spot.tags.forEach { tag ->
                    val tagView = LayoutInflater.from(context).inflate(com.cmc.design.R.layout.view_tag_blue, layoutTagContainer, false)
                    "#$tag".also { tagView.findViewById<TextView>(com.cmc.design.R.id.tv_tag).text = it }
                    layoutTagContainer.addView(tagView)
                }

                Glide.with(this.root)
                    .load(spot.images[0])
                    .into(ivSpotImageFirst)

                Glide.with(this.root)
                    .load(spot.images[1])
                    .into(ivSpotImageSecond)

                ivSpotArchive.setOnClickListener {
                    ivSpotArchive.animateClickEffect()
                    onArchiveClick.invoke(spot.spotId)
                }
                ivSpotImageFirst.setOnClickListener { onImageClick.invoke(spot.spotId) }
                ivSpotImageSecond.setOnClickListener { onImageClick.invoke(spot.spotId) }
            }
        }
    }

    class ScrollImageViewHolder(private val binding: ViewSpotScrollImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            spot: SpotWithMapUiModel,
            onArchiveClick: (Int) -> Unit,
            onImageClick: (Int) -> Unit
        ) {
            with(binding) {
                val context = root.context

                val category = SpotCategoryItem(SpotCategory.fromId(spot.categoryId))

                tvRecommendLabel.isVisible = SpotCategory.isRecommended(spot.categoryId)
                tvSpotTitle.text = spot.spotName
                tvCategory.text = context.getString(category.getName())
                category.getIcon()?.let { ivCategory.setImageResource(it) }
                ivSpotArchive.isSelected = spot.isScraped
                tvDistance.text = DistanceFormatter.formatDistance(spot.distance)
                "${spot.address} ${spot.addressDetail}".also { tvSpotLocation.text = it }

                layoutTagContainer.removeAllViews()
                spot.tags.forEach { tag ->
                    val tagView = LayoutInflater.from(context).inflate(com.cmc.design.R.layout.view_tag_blue, layoutTagContainer, false)
                    "#$tag".also { tagView.findViewById<TextView>(com.cmc.design.R.id.tv_tag).text = it }
                    layoutTagContainer.addView(tagView)
                }

                ivSpotArchive.setOnClickListener {
                    ivSpotArchive.animateClickEffect()
                    onArchiveClick.invoke(spot.spotId)
                }

                rvSpotImage.apply {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = HorizontalImageAdapter(spot.images) { onImageClick.invoke(spot.spotId) }
                    if (rvSpotImage.itemDecorationCount == 0) {
                        addItemDecoration(HorizontalSpaceItemDecoration(8.dp))
                    }
                }
            }
        }
    }


    class DiffCallback(
        private val oldList: List<SpotWithMapUiModel>,
        private val newList: List<SpotWithMapUiModel>
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

    companion object {
        private const val VIEW_TYPE_SINGLE = 1
        private const val VIEW_TYPE_DOUBLE = 2
        private const val VIEW_TYPE_SCROLL = 3

        private const val THRESHOLD_SCROLL = 2 // 이 값을 변경하면 가로 스크롤 기준이 조정됨
    }
}