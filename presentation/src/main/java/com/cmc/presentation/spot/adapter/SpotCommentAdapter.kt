package com.cmc.presentation.spot.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmc.presentation.databinding.ViewCommentBinding
import com.cmc.presentation.spot.model.ReviewUiModel

class SpotCommentAdapter
    : RecyclerView.Adapter<SpotCommentAdapter.SpotCommentViewHolder>() {

    private val items: List<ReviewUiModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotCommentViewHolder {
        val binding = ViewCommentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SpotCommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpotCommentViewHolder, position: Int) {
        val comment = items[position]
        comment.let { holder.bind(comment) }
    }

    override fun getItemCount(): Int = items.size

    class SpotCommentViewHolder(private val binding: ViewCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReviewUiModel) {
            with(binding) {
                tvCommentPoster.text = item.memberName
                tvCommentDesc.text = item.reviewText
                // TODO: Date 값 추가 시, 변경
                tvCommentDate.text = buildString {
                    append("24.07.10")
                    append("  ")
                    append("00:48")
                }
            }
        }
    }
}