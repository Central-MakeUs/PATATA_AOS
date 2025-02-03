package com.cmc.design.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.cmc.design.R
import com.cmc.design.databinding.ViewSpotHorizontalCardBinding

class SpotHorizontalCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewSpotHorizontalCardBinding =
        ViewSpotHorizontalCardBinding.inflate(LayoutInflater.from(context), this, true)

    private var onArchiveClickListener: (() -> Unit)? = null
    private var onCardClickListener: (() -> Unit)? = null

    init {
        initListeners()
    }

    private fun initListeners() {
        // 아카이브 버튼 클릭 리스너
        binding.ivSpotArchive.setOnClickListener {
            binding.ivSpotArchive.isSelected = binding.ivSpotArchive.isSelected.not()
            onArchiveClickListener?.invoke()
        }

        // 카드 전체 클릭 리스너
        binding.root.setOnClickListener {
            onCardClickListener?.invoke()
        }
    }

    /**
     * 카드뷰 세팅
     */
    fun setHorizontalCardView(
        imageResId: Int,
        title: String,
        location: String,
        archiveCount: Int,
        commentCount: Int,
        tags: List<String>,
        isArchived: Boolean = false,
        isRecommended: Boolean = false,
        archiveClickListener: () -> Unit,
        cardClickListener: () -> Unit,
    ) {
        binding.ivSpotImage.setImageResource(imageResId)
        binding.tvSpotLocation.text = location
        binding.tvSpotTitle.text = title
        binding.tvArchiveCount.text = archiveCount.toString()
        binding.tvReviewCount.text = commentCount.toString()
        binding.ivSpotArchive.isSelected = isArchived

        binding.tvRecommendLabel.visibility = if (isRecommended) View.VISIBLE else View.GONE

        onArchiveClickListener = archiveClickListener
        onCardClickListener = cardClickListener

        updateTags(tags)
    }

    /**
     * 태그 업데이트 메서드
     */
    private fun updateTags(tags: List<String>) {
        binding.layoutTagContainer.removeAllViews()
        tags.forEach { tag ->
            val tagView = LayoutInflater.from(context).inflate(R.layout.view_tag_blue, binding.layoutTagContainer, false)
            tagView.findViewById<TextView>(R.id.tv_tag).text = tag
            binding.layoutTagContainer.addView(tagView)
        }
    }

    data class SpotHorizontalCardItem(
        val title: String,
        val location: String,
        val imageResId: Int,
        val category: String,
        val isArchived: Boolean,
        val archiveCount: Int,
        val commentCount: Int,
        val tags: List<String>,
        val isRecommended: Boolean = false
    )
}
