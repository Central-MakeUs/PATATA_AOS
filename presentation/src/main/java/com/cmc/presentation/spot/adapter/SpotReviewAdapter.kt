package com.cmc.presentation.spot.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmc.presentation.databinding.ViewCommentBinding
import com.cmc.presentation.spot.model.ReviewUiModel
import com.example.common.util.DateTimeFormatterUtil.formatUtcToLocal

class SpotReviewAdapter : RecyclerView.Adapter<SpotReviewAdapter.SpotCommentViewHolder>() {

    private var items: List<ReviewUiModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotCommentViewHolder {
        val binding = ViewCommentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SpotCommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpotCommentViewHolder, position: Int) {
        val comment = items[position]
        holder.bind(comment)
    }

    override fun getItemCount(): Int = items.size

    fun setItems(itemList: List<ReviewUiModel>) {
        items = itemList
        notifyDataSetChanged()
    }

    class SpotCommentViewHolder(private val binding: ViewCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReviewUiModel) {
            with(binding) {
                tvCommentPoster.text = item.memberName
                tvCommentDesc.text = item.reviewText
//                tvCommentDate.text = formatUtcToLocal(item.created)
                tvCommentDate.text = formatUtcToLocal("2024-02-03T14:30:00Z")
            }
        }
    }
}