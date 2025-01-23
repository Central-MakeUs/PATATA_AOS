package com.cmc.presentation.spot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cmc.presentation.databinding.ViewCommentBinding

class SpotCommentAdapter(
    private val commentList: List<CommentUiModel>
): RecyclerView.Adapter<SpotCommentAdapter.SpotCommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotCommentViewHolder {
        val binding = ViewCommentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SpotCommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpotCommentViewHolder, position: Int) {
        val comment = commentList[position]
        comment.let { holder.bind(comment) }
    }

    override fun getItemCount(): Int = commentList.size
    class SpotCommentViewHolder(private val binding: ViewCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CommentUiModel) {
            with(binding) {
                tvCommentPoster.text = item.poster
                tvCommentDesc.text = item.description
                tvCommentDate1.text = item.date
            }
        }
    }
}