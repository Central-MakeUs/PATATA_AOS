package com.cmc.presentation.spot.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.cmc.presentation.R
import com.cmc.presentation.databinding.ViewCommentBinding
import com.cmc.presentation.spot.model.ReviewUiModel
import com.example.common.util.DateTimeFormatterUtil.formatUtcToLocal

class SpotReviewAdapter(
    private val onDeleteClick: (Int) -> Unit,
) : RecyclerView.Adapter<SpotReviewAdapter.SpotCommentViewHolder>() {

    private var items: List<ReviewUiModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotCommentViewHolder {
        val binding = ViewCommentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SpotCommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpotCommentViewHolder, position: Int) {
        val comment = items[position]
        holder.bind(comment, onDeleteClick)
    }

    override fun getItemCount(): Int = items.size

    fun setItems(itemList: List<ReviewUiModel>) {
        items = itemList
        notifyDataSetChanged()
    }

    class SpotCommentViewHolder(private val binding: ViewCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReviewUiModel, onDeleteClick: (Int) -> Unit) {
            with(binding) {
                tvCommentPoster.text = item.memberName
                val textColor = if (item.isAuthor) com.cmc.design.R.color.blue_100 else com.cmc.design.R.color.text_sub
                tvCommentPoster.setTextColor(root.context.getColor(textColor))
                tvCommentDesc.text = item.reviewText
                tvCommentDate.text = formatUtcToLocal(item.reviewDate)
                tvCommentDelete.isVisible = item.isAuthor
                tvCommentDelete.setOnClickListener { onDeleteClick.invoke(item.reviewId) }
            }
        }
    }
}