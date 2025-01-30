package com.cmc.presentation.archive

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cmc.presentation.R
import com.cmc.presentation.databinding.ViewArchivePhotoBinding

class ArchivePhotoAdapter(
    private var items: List<Pair<Int, String>>,
    private val isSelectionMode: () -> Boolean, // 선택 모드 상태를 가져오는 람다
    private val isSelected: (Int) -> Boolean, // 아이템이 선택되었는지 확인하는 람다
    private val onPhotoClick: (Int) -> Unit, // 클릭 이벤트 콜백
) : RecyclerView.Adapter<ArchivePhotoAdapter.ArchivePhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchivePhotoViewHolder {
        val binding = ViewArchivePhotoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ArchivePhotoViewHolder(binding, isSelectionMode, isSelected, onPhotoClick)
    }

    override fun onBindViewHolder(holder: ArchivePhotoViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ArchivePhotoViewHolder(
        private val binding: ViewArchivePhotoBinding,
        private val isSelectionMode: () -> Boolean,
        private val isSelected: (Int) -> Boolean,
        private val onPhotoClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Pair<Int, String>) {
            Glide.with(binding.root)
                .load(item.second)
                .placeholder(com.cmc.design.R.drawable.img_sample)
                .into(binding.ivPhoto)

            // UI 업데이트
            val selectionMode = isSelectionMode()
            val selected = isSelected(item.first)

            binding.ivCheck.isVisible = selectionMode
            binding.ivCheck.setImageResource(
                if (selected) R.drawable.ic_circle_checked else R.drawable.ic_circle_unchecked
            )

            // 클릭 이벤트
            binding.root.setOnClickListener {
                onPhotoClick(item.first)
            }
        }
    }
}
