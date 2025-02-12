package com.cmc.presentation.spot.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cmc.presentation.databinding.ViewImageSliderBinding

class ImageSliderAdapter: RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {

    private var images: List<String> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ViewImageSliderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = images[position]
        holder.bind(item, images)
    }

    override fun getItemCount(): Int = images.size

    fun setItems(imageList: List<String>) {
        images = imageList
        notifyDataSetChanged()
    }

    class ImageViewHolder(private val binding: ViewImageSliderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: String, images: List<String>) {
            with(binding) {
                Glide.with(ivImage.context)
                    .load(image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivImage)
            }

            preloadNextImages(binding.root.context, absoluteAdapterPosition, images)
        }

        private fun preloadNextImages(context: Context, position: Int, images: List<String>) {
            val endPoint = if (position + 3 > images.size) images.size else position + 3
            images.subList(position + 1, endPoint).forEach {
                Glide.with(context)
                    .load(it)
                    .preload()
            }
        }
    }
}