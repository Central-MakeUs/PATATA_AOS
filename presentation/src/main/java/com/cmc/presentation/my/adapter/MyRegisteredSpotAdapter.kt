package com.cmc.presentation.my.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cmc.presentation.databinding.ViewMyRegisteredSpotBinding

class MyRegisteredSpotAdapter(
    private val onPhotoClick: (Int) -> Unit,
) : RecyclerView.Adapter<MyRegisteredSpotAdapter.MyRegisteredSpotViewHolder>() {

    private  var items: List<String> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRegisteredSpotViewHolder {
        val binding = ViewMyRegisteredSpotBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyRegisteredSpotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyRegisteredSpotViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<String>) {
        val diffCallback = DiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    class MyRegisteredSpotViewHolder(
        private val binding: ViewMyRegisteredSpotBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            Glide.with(binding.root)
                .load(item)
                .placeholder(com.cmc.design.R.drawable.img_sample)
                .into(binding.ivPhoto)
        }
    }

    class DiffCallback(
        private val oldList: List<String>,
        private val newList: List<String>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}