package com.cmc.presentation.my.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cmc.domain.feature.spot.model.SpotPreview
import com.cmc.presentation.databinding.ViewMyRegisteredSpotBinding
import com.cmc.presentation.spot.model.SpotPreviewUiModel

class MyRegisteredSpotAdapter(
    private val onImageClick: (Int) -> Unit,
) : RecyclerView.Adapter<MyRegisteredSpotAdapter.MyRegisteredSpotViewHolder>() {

    private  var items: List<SpotPreviewUiModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRegisteredSpotViewHolder {
        val binding = ViewMyRegisteredSpotBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyRegisteredSpotViewHolder(binding, onImageClick)
    }

    override fun onBindViewHolder(holder: MyRegisteredSpotViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<SpotPreviewUiModel>) {
        val diffCallback = DiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    class MyRegisteredSpotViewHolder(
        private val binding: ViewMyRegisteredSpotBinding,
        private val onImageClick: (Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SpotPreviewUiModel) {
            Glide.with(binding.root)
                .load(item.representativeImageUrl)
                .into(binding.ivPhoto)

            binding.ivPhoto.setOnClickListener {
                onImageClick.invoke(item.spotId)
            }
        }
    }

    class DiffCallback(
        private val oldList: List<SpotPreviewUiModel>,
        private val newList: List<SpotPreviewUiModel>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].spotId == newList[newItemPosition].spotId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}