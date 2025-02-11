package com.cmc.presentation.spot.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.bumptech.glide.Glide
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
        imageUrl: String,
        title: String,
        location: String,
        archiveCount: Int,
        commentCount: Int,
        tags: List<String>,
        isScraped: Boolean = false,
        isRecommended: Boolean = false,
        archiveClickListener: () -> Unit,
        cardClickListener: () -> Unit,
    ) {
        Glide.with(binding.root)
            .load(imageUrl)
            .placeholder(R.drawable.img_sample)
            .into(binding.ivSpotImage)
        binding.tvSpotLocation.text = location
        binding.tvSpotTitle.text = title
        binding.tvArchiveCount.text = archiveCount.toString()
        binding.tvReviewCount.text = commentCount.toString()
        binding.ivSpotArchive.isSelected = isScraped

        binding.tvRecommendLabel.visibility = if (isRecommended) View.VISIBLE else View.GONE

        onArchiveClickListener = archiveClickListener
        onCardClickListener = cardClickListener

        updateTags(tags)
    }

    /*
    * 스크랩 상태 변경
    * */
    fun updateScrapState(isScraped: Boolean, scrapCount: Int) {
        binding.ivSpotArchive.isSelected = isScraped
        scrapCount.toString().also { binding.tvArchiveCount.text = it }
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
}
