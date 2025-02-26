package com.cmc.presentation.map.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cmc.domain.model.ImageMetadata
import com.cmc.presentation.databinding.ViewSelectedImageBinding
import java.util.Collections

class SelectedImageAdapter(
    private val isEditable: Boolean = true,
    private val onRemoveImage: (ImageMetadata) -> Unit,
) : RecyclerView.Adapter<SelectedImageAdapter.SelectedImageViewHolder>() {

    private val images = mutableListOf<ImageMetadata>()

    fun updateImages(newImages: List<ImageMetadata>) {
        images.clear()
        images.addAll(newImages)
        notifyDataSetChanged()
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(images, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(images, i, i - 1)
            }
        }

        notifyItemMoved(fromPosition, toPosition)
    }

    fun getImages(): List<ImageMetadata> = images.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImageViewHolder {
        val binding = ViewSelectedImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectedImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectedImageViewHolder, position: Int) {
        val imageUri = images[position]
        holder.bind(imageUri)
    }

    override fun getItemCount(): Int = images.size

    inner class SelectedImageViewHolder(private val binding: ViewSelectedImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: ImageMetadata) {
            binding.layoutRepresentsImage.isVisible = absoluteAdapterPosition == 0
            binding.ivDeleteButton.isVisible = isEditable
            binding.ivDeleteButton.setOnClickListener { onRemoveImage(image) }
            Glide.with(binding.root)
                .load(Uri.parse(image.uri))
                .into(binding.ivSelectedImage)
        }
    }
}