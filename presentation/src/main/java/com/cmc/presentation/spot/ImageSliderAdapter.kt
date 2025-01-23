package com.cmc.presentation.spot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cmc.presentation.databinding.ViewImageSliderBinding

class ImageSliderAdapter(
    private val itemList: List<ImageViewPagerModel>
): RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ViewImageSliderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = itemList.size

    class ImageViewHolder(private val binding: ViewImageSliderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ImageViewPagerModel) {
            with(binding) {
                Glide.with(root)
                    .load(item.url)
                    .placeholder(com.cmc.design.R.drawable.img_sample)
                    .into(ivImage)

                tvContact.text = item.contact
                tvContact.setCompoundDrawablesWithIntrinsicBounds(item.contactResId, 0, 0, 0)

                viewSpotBadge.root.isVisible = item.isRecommended
                viewSpotBadge.viewCornerFold.isVisible = false
            }
        }
    }
}