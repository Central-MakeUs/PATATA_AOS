package com.cmc.presentation.home.adapter

import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cmc.design.util.Util.dp

class HorizontalImageAdapter(
    private val images: List<String>,
    private val onImageClick: () -> Unit
) : RecyclerView.Adapter<HorizontalImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(140.dp, 140.dp) // 고정 크기
            scaleType = ImageView.ScaleType.CENTER_CROP
            background = AppCompatResources.getDrawable(this.context, com.cmc.design.R.drawable.bg_rounded_8)
            clipToOutline = true
        }
        return ImageViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(holder.imageView.context)
            .load(images[position])
            .thumbnail(0.25f)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .onlyRetrieveFromCache(false)
            .into(holder.imageView)
        holder.imageView.setOnClickListener { onImageClick.invoke() }
    }

    override fun getItemCount(): Int = images.size

    class ImageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
}
