package com.cmc.presentation.map

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.cmc.presentation.databinding.ViewSelectedImageBinding
import java.util.Collections

class SelectedImageAdapter(
    private val onRemoveImage: (Uri) -> Unit
) : RecyclerView.Adapter<SelectedImageAdapter.SelectedImageViewHolder>() {

    private val images = mutableListOf<Uri>()

    fun updateImages(newImages: List<Uri>) {
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

    fun getImages(): List<Uri> = images

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
        fun bind(uri: Uri) {
            binding.layoutRepresentsImage.isVisible = absoluteAdapterPosition == 0
            binding.ivSelectedImage.setImageURI(uri)
            binding.ivDeleteButton.setOnClickListener { onRemoveImage(uri) }
        }
    }
}